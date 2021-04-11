package net.runeduniverse.libs.rogm.pattern;

import java.util.Map;

import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataNode;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataRelation;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Relation;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFNode;

public interface IRelationPattern extends IPattern {
	String getLabel();

	Relation createFilter(IFNode caller, Direction direction);

	IDataRelation save(Object entity, IDataNode caller, Direction direction, Map<Object, IDataContainer> includedData,
			Integer depth) throws Exception;
}
