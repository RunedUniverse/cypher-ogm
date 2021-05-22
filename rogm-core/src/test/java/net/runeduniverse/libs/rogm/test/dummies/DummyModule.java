package net.runeduniverse.libs.rogm.test.dummies;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.modules.AModule;

public class DummyModule extends AModule {

	@Override
	public Instance<?> build(Configuration cnf) {
		return null;
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
