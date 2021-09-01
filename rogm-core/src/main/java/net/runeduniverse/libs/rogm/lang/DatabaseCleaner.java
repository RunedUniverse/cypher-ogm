package net.runeduniverse.libs.rogm.lang;

import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainManager;

public interface DatabaseCleaner {

	String getChainLabel();

	void setupChainManager(ChainManager chainManager) throws Exception;
}
