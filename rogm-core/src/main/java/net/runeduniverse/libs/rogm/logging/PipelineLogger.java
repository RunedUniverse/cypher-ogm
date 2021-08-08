package net.runeduniverse.libs.rogm.logging;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class PipelineLogger extends ALogger {

	private static final AtomicLong id = new AtomicLong(0);

	private final String prefix;

	public PipelineLogger(Class<?> clazz, Logger parent) {
		super("ROGM", null, parent);
		prefix = "[" + clazz.getSimpleName() + '|' + id.getAndIncrement() + "] ";
	}

	@Override
	public void log(LogRecord record) {
		record.setMessage(this.prefix + record.getMessage());
		super.log(record);
	}
}
