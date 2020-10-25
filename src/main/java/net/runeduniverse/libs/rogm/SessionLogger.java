package net.runeduniverse.libs.rogm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class SessionLogger extends Logger {

	private static final AtomicLong id = new AtomicLong(0);

	private final String prefix;

	public SessionLogger(Class<?> clazz, boolean debug, Logger parent) {
		super(clazz.getCanonicalName(), null);
		prefix = "[ROGM|" + clazz.getSimpleName() + '|' + id + "] ";
		super.setLevel(debug ? Level.ALL : Level.CONFIG);
		if (parent == null)
			super.setParent(Logger.getLogger(Logger.GLOBAL_LOGGER_NAME));
		else
			super.setParent(parent);
	}

	@Override
	public void log(LogRecord record) {
		record.setMessage(this.prefix + record.getMessage());
		super.log(record);
	}

	public void config(Configuration cnf) {
		DatabaseType dbt = cnf.getDbType();
		List<String> msg = new ArrayList<String>();
		msg.add("Initializing Session (debug=" + (cnf.isDebug() ? "true" : "false") + ")");
		msg.add("Database: " + dbt.toString());
		msg.add("Uri: " + cnf.getUri());
		msg.add("Protocol: " + cnf.getProtocol());
		msg.add("Port: " + cnf.getPort());
		msg.add("User: " + cnf.getUser());
		msg.add("Buffer: " + cnf.getBuffer().getClass().getSimpleName());
		msg.add("Module Packages:");
		for (String pkg : cnf.getPkgs())
			msg.add(" - " + pkg);

		this.log(Level.CONFIG, String.join("\n", msg));
	}

}
