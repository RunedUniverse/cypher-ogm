package net.runeduniverse.libs.rogm.modules;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.info.ConnectionInfo;

public interface Module extends PassiveModule, IdTypeResolver {

	// cnf might not get used from every module
	// but is provided if needed
	Instance<?> build(Configuration cnf);

	public interface Instance<ID extends Serializable> {
		boolean connect(ConnectionInfo info);

		boolean disconnect();

		boolean isConnected();

		IRawRecord query(String qry);

		IRawDataRecord queryObject(String qry);

		IRawIdRecord execute(String qry);
	}

	public static interface IRawRecord {
		// return the raw data
		List<Map<String, Object>> getRawData();
	}

	public static interface IRawDataRecord {
		// returns a Map with the ALIAS as Key and DATA as Value
		List<Map<String, Data>> getData();
	}

	public static interface IRawIdRecord {
		// returns a Map with the ALIAS and the IDs
		Map<String, Serializable> getIds();
	}

	public interface Data {
		Serializable getId();

		String getEntityId();

		Set<String> getLabels();

		String getData();

		String getAlias();
	}
}
