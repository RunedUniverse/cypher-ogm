package net.runeduniverse.libs.rogm.pipeline;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.libs.rogm.Session;
import net.runeduniverse.libs.rogm.logging.PipelineLogger;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainManager;

public final class Pipeline implements AutoCloseable {
	private final APipelineFactory<?> factory;
	private final ChainManager chain;
	@Getter
	private final PipelineLogger logger;
	private boolean setupMissing = true;
	private Set<Session> activeSessions = new HashSet<>();

	public Pipeline(APipelineFactory<?> pipelineFactory) {
		this.factory = pipelineFactory;
		this.logger = new PipelineLogger(Pipeline.class, this.factory.getLogger());
		this.chain = new ChainManager(this.logger);
	}

	public Session buildSession() throws Exception {
		if (setupMissing) {
			this.factory.setup(this, this.chain);
			this.setupMissing = false;
		}
		return this.factory.buildSession();
	}

	/**
	 * registers a <code>Session</code> as active
	 * 
	 * @deprecated for internal use only!
	 * @param session
	 */
	@Deprecated
	public void registerActiveSession(Session session) {
		this.activeSessions.add(session);
	}

	@Override
	public void close() throws Exception {
		this.factory.closeConnections();
	}

	public void closeConnections(SessionWrapper sessionWrapper) throws Exception {
		this.activeSessions.remove(sessionWrapper);
		if (this.activeSessions.isEmpty())
			this.close();
	}

	// FLOW
	// factory.setup();
	// factory.isConnected();
	// factory.stop();
}
