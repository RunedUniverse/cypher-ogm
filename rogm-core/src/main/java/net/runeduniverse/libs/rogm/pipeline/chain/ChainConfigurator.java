package net.runeduniverse.libs.rogm.pipeline.chain;

import net.runeduniverse.libs.chain.ChainManager;

public interface ChainConfigurator {

	void setupChainManager(ChainManager chainManager) throws Exception;

}
