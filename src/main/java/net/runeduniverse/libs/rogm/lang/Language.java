package net.runeduniverse.libs.rogm.lang;

import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.Filter;

public interface Language {
	String buildQuery(Filter filter, Parser parser) throws Exception;
}
