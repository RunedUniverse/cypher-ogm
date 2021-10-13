package net.runeduniverse.libs.rogm.logging;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.LogRecord;
import net.runeduniverse.libs.rogm.info.SessionInfo;

public final class SessionLogger extends ALogger {

	private static final AtomicLong id = new AtomicLong(0);

	private final String prefix;

	public SessionLogger(Class<?> clazz, PipelineLogger pipelineLogger) {
		super("ROGM", null, pipelineLogger);
		prefix = "[" + clazz.getSimpleName() + '|' + id.getAndIncrement() + "] ";
	}

	@Override
	public void log(LogRecord record) {
		record.setMessage(this.prefix + record.getMessage());
		super.log(record);
	}

	public SessionLogger logSessionInfo(final SessionInfo info) {
		super.log(Level.CONFIG, this.prefix + '\n' + info.toString());
		return this;
	}
}
