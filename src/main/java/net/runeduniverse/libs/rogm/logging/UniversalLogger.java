package net.runeduniverse.libs.rogm.logging;

import java.util.logging.Logger;

public class UniversalLogger extends ALogger {

	private final Class<?> clazz;

	public UniversalLogger(Class<?> clazz, Logger parent) {
		super(clazz.getSimpleName(), null, parent);
		this.clazz = clazz;
	}

	public Exception throwing(String sourceMethod, Exception thrown) {
		super.throwing(clazz.getName(), sourceMethod, thrown);
		return thrown;
	}

	public void burying(String sourceMethod, Exception exception) {
		super.log(Level.BURY, sourceMethod + "\n" + exception);
	}
}
