package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.logging.Level;

public class ChainLogger extends Logger {

	public ChainLogger(Logger parent) {
		super("ChainLogger", null);
		super.setParent(parent);
	}

	public <E extends Exception> E throwing(Class<?> clazz, String sourceMethod, E thrown) {
		super.throwing(clazz.getName(), sourceMethod, thrown);
		return thrown;
	}

	public void burying(String sourceMethod, Exception exception) {
		super.log(Level.BURY, sourceMethod + "\n" + exception);
	}

	public void logTrace(ChainRuntimeExecutionTrace trace) {
		super.log(Level.FINE, trace.toString());
	}
}
