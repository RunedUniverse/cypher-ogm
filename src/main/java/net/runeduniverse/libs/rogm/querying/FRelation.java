package net.runeduniverse.libs.rogm.querying;

public interface FRelation extends Filter, LabelHolder{
	Filter getStart();
	Filter getTarget();
	Direction getDirection();
	
	public enum Direction{
		OUTGOING, INCOMING, BIDIRECTIONAL
	}
}
