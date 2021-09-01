package net.runeduniverse.libs.rogm.test.dummies;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;

public class DummyLanguage implements Language {

	@Override
	public Instance build(Parser.Instance parser, Module module) {
		return null;
	}

	@Override
	public String getChainLabel() {
		return null;
	}

}
