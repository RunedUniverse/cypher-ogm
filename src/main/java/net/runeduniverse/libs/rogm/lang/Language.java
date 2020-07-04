package net.runeduniverse.libs.rogm.lang;

import java.io.Serializable;
import java.util.Map;

import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.DataHolder;
import net.runeduniverse.libs.rogm.querying.Filter;
import net.runeduniverse.libs.rogm.util.Buffer;
import net.runeduniverse.libs.rogm.util.FieldAccessor;

public interface Language {
	String buildQuery(Filter filter, Parser parser) throws Exception;

	Mapper buildInsert(DataFilter node, Parser parser) throws Exception;

	Mapper buildUpdate(DataFilter node, Parser parser) throws Exception;

	public interface DataFilter extends Filter, DataHolder {
	}

	public interface Mapper {
		String qry();

		<ID extends Serializable> void updateObjectIds(FieldAccessor accessor, Buffer nodeBuffer, Map<String, ID> ids);
	}
}
