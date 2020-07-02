package net.runeduniverse.libs.rogm.lang;

import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.DataHolder;
import net.runeduniverse.libs.rogm.querying.Filter;

public interface Language {
	String buildQuery(Filter filter, Parser parser) throws Exception;
	String buildInsert() throws Exception;
	String buildUpdate(DataFilter node, Parser parser) throws Exception;
	
	public interface DataFilter extends Filter, DataHolder{
	}
}
