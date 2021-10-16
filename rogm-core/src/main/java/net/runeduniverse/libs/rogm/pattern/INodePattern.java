package net.runeduniverse.libs.rogm.pattern;

import java.util.Collection;
import java.util.Map;

import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.RelationQueryBuilder;

public interface INodePattern<B extends NodeQueryBuilder> extends IBaseQueryPattern<B> {

	NodeQueryBuilder search(RelationQueryBuilder caller, boolean lazy);

	NodeQueryBuilder save(Object entity, Map<Object, IQueryBuilder<?, ?, ? extends IFilter>> includedData,
			Integer depth) throws Exception;

	void deleteRelations(Object entity, Collection<Object> deletedEntities);
}
