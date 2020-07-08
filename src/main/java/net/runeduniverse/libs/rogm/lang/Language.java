package net.runeduniverse.libs.rogm.lang;

import java.io.Serializable;
import java.util.Map;

import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.util.Buffer;
import net.runeduniverse.libs.rogm.util.FieldAccessor;

public interface Language {
	String buildQuery(IFilter filter, Parser parser) throws Exception;

	Mapper buildSave(DataFilter node, Parser parser) throws Exception;

	public interface DataFilter extends IFilter, IDataContainer {
	}

	public interface Mapper {
		String qry();

		<ID extends Serializable> void updateObjectIds(FieldAccessor accessor, Buffer nodeBuffer, Map<String, ID> ids);
	}
}
