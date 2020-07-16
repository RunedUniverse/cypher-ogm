package net.runeduniverse.libs.rogm.querying;

import net.runeduniverse.libs.rogm.annotations.Direction;

public interface IFRelation extends IFilter, ILabeled{
	IFNode getStart();
	IFNode getTarget();
	Direction getDirection();
}
