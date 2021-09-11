package net.runeduniverse.libs.rogm.test.dummies;

import java.io.Serializable;

import net.runeduniverse.libs.rogm.info.ConnectionInfo;
import net.runeduniverse.libs.rogm.modules.Module.IRawDataRecord;
import net.runeduniverse.libs.rogm.modules.Module.IRawIdRecord;
import net.runeduniverse.libs.rogm.modules.Module.IRawRecord;
import net.runeduniverse.libs.rogm.modules.Module.Instance;

public class DummyModuleInstance implements Instance<Serializable> {

	@Override
	public boolean connect(ConnectionInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IRawRecord query(String qry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRawDataRecord queryObject(String qry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRawIdRecord execute(String qry) {
		// TODO Auto-generated method stub
		return null;
	}

}
