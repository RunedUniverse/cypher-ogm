package net.runeduniverse.libs.rogm.lang.cypher;

import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.lang.cypher.pipeline.chains.CleanupLayers;
import net.runeduniverse.libs.rogm.lang.cypher.pipeline.chains.CypherChains;
import net.runeduniverse.libs.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.modules.IdTypeResolver;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.chain.ChainManager;

public class CypherLanguage implements Language {

	@Override
	public Instance build(final Logger logger, final IdTypeResolver resolver, final Parser.Instance parser) {
		return new CypherInstance(resolver, parser, new UniversalLogger(CypherInstance.class, logger));
	}

	public String getChainLabel() {
		return CypherChains.DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS.LABEL;
	}

	@Override
	public void setupChainManager(ChainManager chainManager) throws Exception {
		chainManager.addChainLayers(CleanupLayers.class);
	}
}
