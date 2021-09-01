package net.runeduniverse.libs.rogm.lang.cypher;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.lang.cypher.pipeline.chains.CleanupLayers;
import net.runeduniverse.libs.rogm.lang.cypher.pipeline.chains.CypherChains;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainManager;

public class CypherLanguage implements Language {

	@Override
	public Instance build(Parser.Instance parser, Module module) {
		return new CypherInstance(parser, module);
	}

	public String getChainLabel() {
		return CypherChains.DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS.LABEL;
	}

	@Override
	public void setupChainManager(ChainManager chainManager) throws Exception {
		chainManager.addChainLayers(CleanupLayers.class);
	}
}
