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

		// return the raw data
		List<Map<String, Object>> query(String qry);

		// returns a Map with the ALIAS as Key and DATA as Value
		List<Map<String, Data>> queryObject(String qry);

		// returns a Map with the ALIAS and the IDs
		Map<String, Serializable> execute(String qry);
	}

	public interface Data {
		Serializable getId();

		String getEntityId();

		Set<String> getLabels();

		String getData();

		String getAlias();
	}
}
