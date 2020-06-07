package net.runeduniverse.libs.rogm.querying;

public interface FRelation extends Filter, ParamFilter{
	Filter getStart();
	Filter getTarget();
	Direction getDirection();
	
	public enum Direction{
		OUTGOING, INCOMING, BIDIRECTIONAL
	}
}
