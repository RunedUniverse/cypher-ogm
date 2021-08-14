package net.runeduniverse.libs.rogm.pipeline;

import net.runeduniverse.libs.rogm.Session;
import net.runeduniverse.libs.rogm.logging.PipelineLogger;

public class Pipeline {
	private final ATransactionFactory factory;
	private final PipelineLogger logger;

	public Pipeline(ATransactionFactory transactionFactory) {
		this.factory = transactionFactory;
		this.logger = new PipelineLogger(Pipeline.class, this.factory.getLogger());
	}

	public Session buildSession() {
		return new SessionWrapper(this.factory, this.logger, this.factory.getSessionInfo());
	}

	// FLOW
	// factory.setup();
	// factory.isConnected();
	// factory.stop();
}
