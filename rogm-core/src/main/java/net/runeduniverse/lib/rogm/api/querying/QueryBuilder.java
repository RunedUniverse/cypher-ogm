package net.runeduniverse.lib.rogm.api.querying;

import net.runeduniverse.lib.rogm.api.pattern.IArchive;
import net.runeduniverse.lib.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.lib.rogm.querying.QueryBuilder.RelationQueryBuilder;

public class QueryBuilder {
	public static Creator<NodeQueryBuilder> CREATOR_NODE_BUILDER;
	public static Creator<RelationQueryBuilder> CREATOR_REALATION_BUILDER;
	
	@FunctionalInterface
	public static interface Creator<BUILDER> {
		BUILDER create(IArchive archive);
	}
}
