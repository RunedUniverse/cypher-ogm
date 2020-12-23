package net.runeduniverse.libs.rogm.test.dummies;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.modules.Module;

public class DummyModule implements Module {

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
