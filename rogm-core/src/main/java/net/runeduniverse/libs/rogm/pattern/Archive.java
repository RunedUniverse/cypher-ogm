package net.runeduniverse.libs.rogm.pattern;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import lombok.Getter;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.logging.Level;
import net.runeduniverse.libs.rogm.modules.PassiveModule;
import net.runeduniverse.libs.rogm.pattern.scanner.TypeScanner;
import net.runeduniverse.libs.rogm.pipeline.EntityFactory;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.scanner.PackageScanner;
import net.runeduniverse.libs.utils.DataHashMap;
import net.runeduniverse.libs.utils.DataMap;
import net.runeduniverse.libs.utils.DataMap.Value;

public final class Archive {
	public static boolean PACKAGE_SCANNER_DEBUG_MODE = false;

	private final DataMap<Class<?>, Set<IPattern>, Set<EntityFactory>> patterns = new DataHashMap<>();
	private final Set<ClassLoader> loader = new HashSet<>();
	private final Set<String> pkgs = new HashSet<>();
	private final PackageScanner.Validator validator = new PackageScanner.Validator() {

		@Override
		public void validate() throws Exception {
			for (Value<Set<IPattern>, ?> pair : patterns.valueSet())
				IValidatable.validate(pair.getValue());
		}
	};
	@Getter
	private final Configuration cnf;
	@Getter
	private final QueryBuilder queryBuilder;

	public Archive(final Configuration cnf) {
		this.cnf = cnf;
		this.loader.addAll(this.cnf.getLoader());
		this.pkgs.addAll(this.cnf.getPkgs());
		this.queryBuilder = new QueryBuilder(this);
	}

	public void scan(TypeScanner... scanner) throws ScannerException {
		new PackageScanner().includeOptions(this.loader, this.pkgs, Arrays.asList(scanner), this.validator)
				.enableDebugMode(PACKAGE_SCANNER_DEBUG_MODE || cnf.getLoggingLevel() != null && cnf.getLoggingLevel()
						.intValue() < Level.INFO.intValue())
				.scan()
				.throwSurpressions(new ScannerException("Pattern parsing failed! See surpressed Exceptions!"));
	}

	public void addEntry(Class<?> type, IPattern pattern, EntityFactory factory) {
		if (!this.patterns.containsKey(type))
			this.patterns.put(type, new HashSet<>(), new HashSet<>());
		this.patterns.get(type)
				.add(pattern);
		this.patterns.getData(type)
				.add(factory);
	}

	public Archive applyConfig() throws ScannerException {
		for (PassiveModule m : this.cnf.getPassiveModules())
			m.configure(this);
		return this;
	}

	public void logPatterns(Logger logger) {
		StringBuilder msg = new StringBuilder("Archive Pattern Dump");
		this.patterns.forEach((c, patterns, factorys) -> {
			msg.append("\n [" + c.getSimpleName() + "] " + c.getCanonicalName());
			msg.append("\n   Pattern:");
			this.appendSetContent(msg, patterns);
			msg.append("\n   EntityFactories:");
			this.appendSetContent(msg, factorys);
		});
		logger.finer(msg.toString());
	}

	private void appendSetContent(StringBuilder builder, Set<?> set) {
		for (Object obj : set) {
			Class<?> clazz = obj.getClass();
			builder.append("\n - [" + clazz.getSimpleName() + "] " + clazz.getCanonicalName());
		}
	}

	// QUERRYING
	public Set<IPattern> getPatterns(Class<?> type) {
		return this.patterns.get(type);
	}

	@SuppressWarnings("unchecked")
	public <P extends IPattern> P getPattern(Class<?> type, Class<P> patternType) {
		for (IPattern pattern : this.patterns.get(type))
			if (patternType.isInstance(pattern))
				return (P) pattern;
		return null;
	}

	public IConverter<?> getIdFieldConverter(Class<?> type) {
		IConverter<?> converter = null;
		for (IPattern p : this.patterns.get(type))
			if (p.getField(Id.class) != null) {
				converter = p.getField(Id.class)
						.getConverter();
				break;
			}
		return IConverter.validate(converter);
	}
}
