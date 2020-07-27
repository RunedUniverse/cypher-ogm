package net.runeduniverse.libs.rogm.lang;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.Data;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IStorage;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface Language {

	Instance build(Parser.Instance parser, Module module);

	public interface Instance {
		IMapper query(IFilter filter) throws Exception;

		IMapper save(IDataContainer container) throws Exception;
	}

	public interface IMapper {
		String qry();

		<ID extends Serializable> void updateObjectIds(IStorage storage, Map<String, ID> ids);

		IPattern.IDataRecord parseDataRecord(List<Map<String, Data>> records);
	}
}
