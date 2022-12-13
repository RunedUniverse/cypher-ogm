package net.runeduniverse.lib.rogm.lang.sql.mariadb;

import java.util.logging.Logger;

import net.runeduniverse.lib.rogm.lang.Language;
import net.runeduniverse.lib.rogm.modules.IdTypeResolver;
import net.runeduniverse.lib.utils.chain.ChainManager;
import net.runeduniverse.lib.utils.logging.UniversalLogger;

public class MariaLanguage implements Language {

	@Override
	public String getChainLabel() {
		return null;
	}

	@Override
	public void setupChainManager(ChainManager chainManager) throws Exception {
	}

	@Override
	public Instance build(Logger logger, IdTypeResolver resolver,
			net.runeduniverse.lib.rogm.parser.Parser.Instance parser) {
		return new MariaInstance(resolver, parser, new UniversalLogger(MariaInstance.class, logger));
	}

}
