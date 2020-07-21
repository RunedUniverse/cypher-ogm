package net.runeduniverse.libs.rogm.lang;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.Data;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface Language {

	Instance build(Parser.Instance parser, Module module);

	public interface Instance {
		IMapper buildQuery(IFilter filter) throws Exception;

		IMapper buildSave(IDataFilter node) throws Exception;
	}

	public interface IDataFilter extends IFilter, IDataContainer {
	}

	public interface IMapper {
		String qry();

		<ID extends Serializable> void updateObjectIds(PatternStorage storage, Map<String, ID> ids);

		IPattern.IDataRecord parseData(List<Map<String, Data>> records);
	}
}
