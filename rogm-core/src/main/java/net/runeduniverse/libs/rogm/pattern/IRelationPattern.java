package net.runeduniverse.libs.rogm.pattern;

import java.util.Map;

import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.RelationQueryBuilder;

public interface IRelationPattern<B extends RelationQueryBuilder> extends IBaseQueryPattern<B>, IValidatable {
	String getLabel();

	RelationQueryBuilder createFilter(NodeQueryBuilder caller, Direction direction);

	RelationQueryBuilder save(Object entity, NodeQueryBuilder caller, Direction direction,
			Map<Object, IQueryBuilder<?, ?, ? extends IFilter>> includedData, Integer depth) throws Exception;
}
