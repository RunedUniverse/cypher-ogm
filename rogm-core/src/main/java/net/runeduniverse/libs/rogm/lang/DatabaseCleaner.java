package net.runeduniverse.libs.rogm.lang;

import net.runeduniverse.libs.rogm.pipeline.chain.ChainConfigurator;

public interface DatabaseCleaner extends ChainConfigurator {

	String getChainLabel();
}
