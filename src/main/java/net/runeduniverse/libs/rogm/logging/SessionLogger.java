package net.runeduniverse.libs.rogm.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.DatabaseType;

public final class SessionLogger extends Logger {

	private static final AtomicLong id = new AtomicLong(0);

	private final String prefix;

	public SessionLogger(Class<?> clazz, Logger parent, Level level) {
		super("ROGM", null);
		prefix = "[" + clazz.getSimpleName() + '|' + id.getAndIncrement() + "] ";

		if (parent == null) {
			parent = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
			parent.setLevel(Level.ALL);
		}
		super.setParent(parent);

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
		DatabaseType dbt = cnf.getDbType();
		List<String> msg = new ArrayList<String>();
		msg.add("Initializing Session");
		msg.add("\tDatabase: " + dbt.toString());
		msg.add("\tUri: " + cnf.getUri());
		msg.add("\tProtocol: " + cnf.getProtocol());
		msg.add("\tPort: " + cnf.getPort());
		msg.add("\tUser: " + cnf.getUser());
		msg.add("\tBuffer: " + cnf.getBuffer().getClass().getSimpleName());
		msg.add("\tModule Packages:");
		for (String pkg : cnf.getPkgs())
			msg.add("\t - " + pkg);

		this.log(Level.CONFIG, String.join("\n", msg));
	}

}
