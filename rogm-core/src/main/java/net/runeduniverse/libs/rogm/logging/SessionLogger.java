package net.runeduniverse.libs.rogm.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.Configuration;

public final class SessionLogger extends ALogger {

	private static final AtomicLong id = new AtomicLong(0);

	private final String prefix;

	public SessionLogger(Class<?> clazz, Logger parent, Level level) {
		super("ROGM", null, parent);
		prefix = "[" + clazz.getSimpleName() + '|' + id.getAndIncrement() + "] ";

		if (level == null)
			super.setLevel(Level.CONFIG);
		else
			super.setLevel(level);
	}

	@Override
	public void log(LogRecord record) {
		record.setMessage(this.prefix + record.getMessage());
		super.log(record);
	}

	public void config(Configuration cnf) {
		List<String> msg = new ArrayList<String>();
		msg.add("Initializing Session");
		msg.add("Database Module: " + cnf.getModule().getClass().getSimpleName());
		msg.add("Uri: " + cnf.getUri());
		msg.add("Protocol: " + cnf.getProtocol());
		msg.add("Port: " + cnf.getPort());
		msg.add("User: " + cnf.getUser());
		msg.add("Buffer: " + cnf.getBuffer().getClass().getSimpleName());
		msg.add("Model Packages:");
		for (String pkg : cnf.getPkgs())
			msg.add(" - " + pkg);

		this.log(Level.CONFIG, String.join("\n\t", msg));
	}
}
