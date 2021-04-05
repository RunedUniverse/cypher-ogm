package net.runeduniverse.libs.rogm.pattern;

import java.util.Collection;

public interface INodePattern extends IPattern{

	void deleteRelations(Object entity, Collection<Object> deletedEntities);
}
