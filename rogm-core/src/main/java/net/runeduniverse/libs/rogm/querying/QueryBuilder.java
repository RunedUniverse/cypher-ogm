package net.runeduniverse.libs.rogm.querying;

import net.runeduniverse.libs.rogm.querying.builder.NodeFilterBuilder;
import net.runeduniverse.libs.rogm.querying.builder.RelationFilterBuilder;

public class QueryBuilder {
	public static NodeFilterBuilder loadNode(Class<?> type) {
		return new NodeFilterBuilder();
	}

	public static RelationFilterBuilder loadRelation(Class<?> type) {
		return new RelationFilterBuilder();

	}
}
