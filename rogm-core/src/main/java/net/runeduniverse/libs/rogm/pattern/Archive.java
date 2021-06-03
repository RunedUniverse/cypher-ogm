package net.runeduniverse.libs.rogm.pattern;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.modules.PassiveModule;
import net.runeduniverse.libs.rogm.pattern.scanner.TypeScanner;
import net.runeduniverse.libs.rogm.pipeline.Assembler;
import net.runeduniverse.libs.rogm.pipeline.EntityFactory;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.scanner.PackageScanner;
import net.runeduniverse.libs.utils.DataHashMap;
import net.runeduniverse.libs.utils.DataMap;
import net.runeduniverse.libs.utils.DataMap.Value;

public final class Archive {
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
	private final Assembler assembler;
	@Getter
	private final QueryBuilder queryBuilder;

	public Archive(final Configuration cnf) {
		this.cnf = cnf;
		this.loader.addAll(this.cnf.getLoader());
		this.pkgs.addAll(this.cnf.getPkgs());
		this.assembler = new Assembler(this);
		this.queryBuilder = new QueryBuilder(this);
	}

	public void scan(TypeScanner... scanner) throws ScannerException {
		new PackageScanner().includeOptions(this.loader, this.pkgs, Arrays.asList(scanner), this.validator)
				/*
				 * .enableDebugMode(cnf.getLoggingLevel() != null && cnf.getLoggingLevel()
				 * .intValue() < Level.INFO.intValue())
				 */
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

	// QUERRYING
	public Set<IPattern> getPatterns(Class<?> type) {
		return this.patterns.get(type);
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
