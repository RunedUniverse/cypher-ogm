package net.runeduniverse.libs.rogm.test.dummies;

import java.util.logging.Logger;

import net.runeduniverse.libs.chain.ChainManager;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.IdTypeResolver;
import net.runeduniverse.libs.rogm.parser.Parser;

public class DummyLanguage implements Language {

	@Override
	public Instance build(Logger logger, IdTypeResolver resolver, Parser.Instance parser) {
		return new DummyLanguageInstance();
	}

	@Override
	public String getChainLabel() {
		return null;
	}

	@Override
	public void setupChainManager(ChainManager chainManager) throws Exception {
		// no cleanup to be added to Chain Manger
	}

}
