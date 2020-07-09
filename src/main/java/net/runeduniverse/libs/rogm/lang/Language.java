package net.runeduniverse.libs.rogm.lang;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.runeduniverse.libs.rogm.modules.Module.Data;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface Language {
	Mapper buildQuery(IFilter filter, Parser parser) throws Exception;

	Mapper buildSave(DataFilter node, Parser parser) throws Exception;

	public interface DataFilter extends IFilter, IDataContainer {
	}

	public interface Mapper {
		String qry();

		<ID extends Serializable> void updateObjectIds(PatternStorage storage, Map<String, ID> ids);
		Object parseObject(Map<String, Data> record);
		Collection<Object> parseObjects(List<Map<String, Data>> records);
	}
}
