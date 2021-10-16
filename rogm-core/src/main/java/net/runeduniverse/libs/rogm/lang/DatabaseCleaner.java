package net.runeduniverse.libs.rogm.lang;

import net.runeduniverse.libs.chain.ChainManager;

public interface DatabaseCleaner {

	String getChainLabel();

	void setupChainManager(ChainManager chainManager) throws Exception;
}
