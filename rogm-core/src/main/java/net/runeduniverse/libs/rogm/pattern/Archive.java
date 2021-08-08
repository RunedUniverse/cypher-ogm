package net.runeduniverse.libs.rogm.pattern;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import lombok.Getter;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.info.PackageInfo;
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
	private final Set<String> pkgs = new HashSet<>();
	private final Set<ClassLoader> loader = new HashSet<>();
	private final PackageScanner.Validator validator = new PackageScanner.Validator() {

		@Override
		public void validate() throws Exception {
			for (Value<Set<IPattern>, ?> pair : patterns.valueSet())
				IValidatable.validate(pair.getValue());
		}
	};
	@Getter
	private final PackageInfo info;
	@Getter
	private final QueryBuilder queryBuilder;

	public Archive(final PackageInfo info) {
		this.info = info;
		this.pkgs.addAll(this.info.getPkgs());
		this.loader.addAll(this.info.getLoader());
		this.queryBuilder = new QueryBuilder(this);
	}

	public void scan(TypeScanner... scanner) throws ScannerException {
		new PackageScanner().includeOptions(this.loader, this.pkgs, Arrays.asList(scanner), this.validator)
				.enableDebugMode(PACKAGE_SCANNER_DEBUG_MODE || info.getLoggingLevel() != null && info.getLoggingLevel()
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
			if (obj == null)
				continue;
			Class<?> clazz = obj.getClass();
			builder.append("\n   - [" + clazz.getSimpleName() + "] " + clazz.getCanonicalName());
		}
	}

	// QUERRYING
	public Set<IPattern> getPatterns(Class<?> type) {
		return this.patterns.get(type);
	}

	public <P extends IPattern> P getPattern(Class<?> type, Class<P> patternType) {
		if (this.patterns.containsKey(type))
			return this._getPattern(this.patterns.get(type), patternType);

		for (Class<?> key : this.patterns.keySet())
			if (type.isInstance(key))
				return this._getPattern(this.patterns.get(key), patternType);
		return null;
	}

	@SuppressWarnings("unchecked")
	private <P extends IPattern> P _getPattern(Set<IPattern> patterns, Class<P> patternType) {
		for (IPattern pattern : patterns)
			if (patternType.isInstance(pattern))
				return (P) pattern;
		return null;
	}

	public IPattern getPattern(Class<?> type, Class<?>... patternTypes) {
		for (IPattern pattern : this.patterns.get(type))
			for (int i = 0; i < patternTypes.length; i++)
				if (patternTypes[i].isInstance(pattern))
					return pattern;
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
