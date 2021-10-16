package net.runeduniverse.libs.rogm.modules.neo4j;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.pipeline.DatabasePipelineFactory;
import net.runeduniverse.libs.chain.ChainManager;

public class DebugDatabasePipelineFactory extends DatabasePipelineFactory {

	public DebugDatabasePipelineFactory(Configuration config) {
		super(config);
	}

	@Override
	protected void setupChainManager(ChainManager chainManager) throws Exception {
		chainManager.addChainLayers(DebugChainLayers.class);
		super.setupChainManager(chainManager);
	}

}
