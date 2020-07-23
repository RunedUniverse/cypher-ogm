package net.runeduniverse.libs.rogm.pattern;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.parser.Parser;

public interface IStorage {

	Parser.Instance getParser();

	Configuration getConfig();

	IPattern getPattern(Class<?> clazz) throws Exception;
}
