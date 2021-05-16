package net.runeduniverse.libs.rogm.pattern;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import net.runeduniverse.libs.rogm.pattern.IPattern.PatternType;
import net.runeduniverse.libs.rogm.pattern.scanner.TypeScanner;
import net.runeduniverse.libs.rogm.pipeline.EntityFactory;
import net.runeduniverse.libs.scanner.MethodPattern;
import net.runeduniverse.libs.scanner.PackageScanner;
import net.runeduniverse.libs.scanner.TypeScanner.ResultConsumer;
import net.runeduniverse.libs.utils.DataHashMap;
import net.runeduniverse.libs.utils.DataMap;
import net.runeduniverse.libs.utils.DataMap.Value;

public final class Archive {
	private final DataMap<Class<?>, Set<IPattern>, Set<EntityFactory>> patterns = new DataHashMap<>();
	private final Set<ClassLoader> loader = new HashSet<>();
	private final Set<String> pkgs = new HashSet<>();
	private final PackageScanner.Validator validator;

	public Archive(Collection<ClassLoader> loader, Collection<String> pkgs) {
		this.loader.addAll(loader);
		this.pkgs.addAll(pkgs);
		this.validator = new PackageScanner.Validator() {

			@Override
			public void validate() throws Exception {
				for (Value<Set<IPattern>, ?> pair : patterns.valueSet())
					IValidatable.validate(pair.getValue());
			}
		};
	}

	public void scan(TypeScanner... scanner) throws Exception {
		DataMap<Class<?>, Set<IPattern>, Set<EntityFactory>> tempPatterns = new DataHashMap<>();
		new PackageScanner()
				.includeOptions(this.loader, this.pkgs, Arrays.asList(scanner),
						new TypeScanner.NodeScanner(this, p -> addEntry(p.getType(), p, null)),
						new TypeScanner.RelationScanner(this, p -> addEntry(p.getType(), p, null)), this.validator)
				/*
				 * .enableDebugMode(cnf.getLoggingLevel() != null && cnf.getLoggingLevel()
				 * .intValue() < Level.INFO.intValue())
				 */
				.scan()
				.throwSurpressions(new Exception("Pattern parsing failed! See surpressed Exceptions!"));
	}

	public void addEntry(Class<?> type, IPattern pattern, EntityFactory factory) {
		if (!this.patterns.containsKey(type))
			this.patterns.put(type, new HashSet<>(), new HashSet<>());
		this.patterns.get(type)
				.add(pattern);
		this.patterns.getData(type)
				.add(factory);
	}
}
