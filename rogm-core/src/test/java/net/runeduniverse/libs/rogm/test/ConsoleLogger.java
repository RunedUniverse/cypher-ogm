package net.runeduniverse.libs.rogm.test;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class ConsoleLogger extends Logger {

	public ConsoleLogger() {
		super("ROGM-DEBUG-CONSOLE", null);
		super.setLevel(Level.ALL);
		super.addHandler(new ConsoleHandler());
	}

	public ConsoleLogger(Logger parent) {
		this();
		super.setParent(parent);
	}

	@Override
	public void log(LogRecord record) {
		System.out.println("[LOGGING-CHAIN-OVERRIDE][" + record.getLevel()
				.getName() + "]\n" + record.getMessage());
		super.log(record);
	}
}
