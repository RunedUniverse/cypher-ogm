package net.runeduniverse.libs.rogm.pipeline;

import net.runeduniverse.libs.rogm.Session;
import net.runeduniverse.libs.rogm.logging.PipelineLogger;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainManager;

public final class Pipeline implements AutoCloseable {
	private final APipelineFactory<?> factory;
	private final ChainManager chain;
	private final PipelineLogger logger;
	private boolean setupMissing = true;

	public Pipeline(APipelineFactory<?> pipelineFactory) {
		this.factory = pipelineFactory;
		this.chain = new ChainManager();
		this.logger = new PipelineLogger(Pipeline.class, this.factory.getLogger());
	}

	public Session buildSession() throws Exception {
		if (setupMissing) {
			this.factory.setup(this.chain);
			this.setupMissing = false;
		}
		return new SessionWrapper(this.factory, this.logger, this.factory.getSessionInfo());
	}

	@Override
	public void close() throws Exception {
		this.factory.closePipeline(this);
	}

	// FLOW
	// factory.setup();
	// factory.isConnected();
	// factory.stop();
}
