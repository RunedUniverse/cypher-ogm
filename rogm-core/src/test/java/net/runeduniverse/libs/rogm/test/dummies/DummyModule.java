package net.runeduniverse.libs.rogm.test.dummies;

import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.modules.AModule;
import net.runeduniverse.libs.rogm.parser.Parser;

public class DummyModule extends AModule {

	@Override
	public Instance<?> build(Logger logger, Parser.Instance parser) {
		return new DummyModuleInstance();
	}

	@Override
	public Class<?> idType() {
		return Number.class;
	}

	@Override
	public boolean checkIdType(Class<?> type) {
		return true;
	}

	@Override
	public String getIdAlias() {
		return "_id";
	}

}
