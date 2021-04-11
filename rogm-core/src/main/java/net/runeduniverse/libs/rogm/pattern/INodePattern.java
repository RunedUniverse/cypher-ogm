package net.runeduniverse.libs.rogm.pattern;

import java.util.Collection;
import java.util.Map;

import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataNode;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;

public interface INodePattern extends IPattern {

	IFNode search(IFRelation caller, boolean lazy);

	IDataNode save(Object entity, Map<Object, IDataContainer> includedData, Integer depth) throws Exception;

	void deleteRelations(Object entity, Collection<Object> deletedEntities);
}
