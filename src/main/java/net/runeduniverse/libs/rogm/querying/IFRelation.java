package net.runeduniverse.libs.rogm.querying;

import net.runeduniverse.libs.rogm.annotations.Direction;

public interface IFRelation extends IFilter, ILabeled{
	IFilter getStart();
	IFilter getTarget();
	Direction getDirection();
}
