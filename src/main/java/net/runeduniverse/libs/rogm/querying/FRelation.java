package net.runeduniverse.libs.rogm.querying;

import net.runeduniverse.libs.rogm.annotations.Direction;

public interface FRelation extends Filter, LabelHolder{
	Filter getStart();
	Filter getTarget();
	Direction getDirection();
}
