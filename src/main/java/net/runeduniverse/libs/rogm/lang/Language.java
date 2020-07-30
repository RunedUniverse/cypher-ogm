package net.runeduniverse.libs.rogm.lang;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.Data;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface Language {

	Instance build(Parser.Instance parser, Module module);

	public interface Instance {
		ILoadMapper load(IFilter filter) throws Exception;

		ISaveMapper save(IDataContainer container, IFilter filter) throws Exception;

		IDeleteMapper delete(IFilter filter, IFRelation relation) throws Exception;

		String deleteRelations(Collection<String> ids);
	}

	public interface IMapper {
		String qry();
	}

	public interface ILoadMapper extends IMapper {
		IPattern.IDataRecord parseDataRecord(List<Map<String, Data>> records);
	}

	public interface ISaveMapper extends IMapper {
		String effectedQry();

		<ID extends Serializable> void updateObjectIds(IBuffer buffer, Map<String, ID> ids);

		Collection<String> reduceIds(IBuffer buffer, List<Map<String, Object>> effectedIds);
	}

	public interface IDeleteMapper extends IMapper {
		String effectedQry();

		void updateBuffer(IBuffer buffer, Serializable deletedId, List<Map<String, Object>> effectedIds);
	}
}
