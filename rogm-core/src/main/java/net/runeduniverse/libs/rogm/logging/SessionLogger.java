package net.runeduniverse.libs.rogm.logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import net.runeduniverse.libs.rogm.info.ConnectionInfo;
import net.runeduniverse.libs.rogm.info.SessionInfo;

public final class SessionLogger extends ALogger {

	private static final AtomicLong id = new AtomicLong(0);

	private final SessionInfo info;
	private final String prefix;

	public SessionLogger(Class<?> clazz, PipelineLogger pipelineLogger, SessionInfo info) {
		super("ROGM", null, pipelineLogger);
		this.info = info;
		prefix = "> [" + clazz.getSimpleName() + '|' + id.getAndIncrement() + "] ";
		super.setLevel(pipelineLogger.getLevel());
	}

	@Override
	public void log(LogRecord record) {
		record.setMessage(this.prefix + record.getMessage());
		super.log(record);
	}

	public SessionLogger logConfig() {
		List<String> msg = new ArrayList<String>();
		msg.add("Initializing Session");
		for (ConnectionInfo conInfo : this.info.getConInfos()) {
			msg.add("Database Module: " + conInfo.getModule()
					.getClass()
					.getSimpleName());
			msg.add(" ├ Uri:      " + conInfo.getUri());
			msg.add(" ├ Protocol: " + conInfo.getProtocol());
			msg.add(" ├ Port:     " + conInfo.getPort());
			msg.add(" └ User:     " + conInfo.getUser());
		}
		msg.add("Model Packages:");
		for (Iterator<String> pkgIter = this.info.getPkgInfo()
				.getPkgs()
				.iterator(); pkgIter.hasNext();) {
			String s = pkgIter.next();
			msg.add((pkgIter.hasNext() ? " ├ " : " └ ") + s);
		}
		msg.add("TransactionBuilder:");
		msg.add(" └ " + this.info.getBuilderClass()
				.getSimpleName());
		msg.add("Buffer:");
		msg.add(" └ " + this.info.getBufferClass()
				.getSimpleName());

		super.log(Level.CONFIG, String.join("\n\t", msg));
		return this;
	}
}
