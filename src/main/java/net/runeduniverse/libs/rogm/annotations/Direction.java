package net.runeduniverse.libs.rogm.annotations;

public enum Direction {
	OUTGOING, INCOMING, BIDIRECTIONAL;
	
	public static Direction opposing(Direction direction) {
		switch (direction) {
		case OUTGOING:
			return INCOMING;
		case INCOMING:
			return OUTGOING;
		case BIDIRECTIONAL:
		default:
			return BIDIRECTIONAL;
		}
	}
}
