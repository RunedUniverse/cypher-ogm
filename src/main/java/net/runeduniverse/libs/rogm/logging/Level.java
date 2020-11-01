package net.runeduniverse.libs.rogm.logging;

public class Level extends java.util.logging.Level {

	protected Level(String name, int value) {
		super(name, value);
	}

	public static final Level BURY = new Level("BURY", 601);

}
