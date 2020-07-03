package net.runeduniverse.libs.rogm.modules;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.util.DataMap;

public interface Module {

	void prepare(Configuration cnf);
	
	// cnf might not get used from every module 
	// but is provided if needed
	Instance<?> build(Configuration cnf);
	
	public interface Instance <ID extends Serializable>{
		boolean connect(Configuration cnf);
		boolean disconnect();
		boolean isConnected();
		
		// return the raw data
		List<Map<String, Object>> query(String qry);
		// returns a Map with the ALIAS, the ID and the DATA
		DataMap<ID, String, String> queryObject(String qry);
		// returns a Map with the ALIAS and the ID
		Map<String, ID> execute(String qry);
	}
}
