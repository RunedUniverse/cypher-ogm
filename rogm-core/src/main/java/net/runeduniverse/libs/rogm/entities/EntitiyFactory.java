package net.runeduniverse.libs.rogm.entities;

import lombok.Getter;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.scanner.PackageScanner;
import net.runeduniverse.libs.utils.DataHashMap;
import net.runeduniverse.libs.utils.DataMap;

public class EntitiyFactory {

	@Getter
	private final Configuration config;
	@Getter
	private final Parser.Instance parser;
	private final UniversalLogger logger;

	private final DataMap<Class<?>, IPattern, IPattern.PatternType> patterns = new DataHashMap<>();

	public EntitiyFactory(Configuration cnf, Parser.Instance parser) throws Exception {
		this.config = cnf;
		this.logger = new UniversalLogger(EntitiyFactory.class, cnf.getLogger());
		this.parser = parser;

		new PackageScanner().includeOptions(cnf.getLoader(), cnf.getPkgs(), cnf.getScanner()).scan();
	}

}
