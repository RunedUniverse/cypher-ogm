package net.runeduniverse.libs.rogm.modules;

import java.io.Serializable;
import java.util.Map;
import net.runeduniverse.libs.rogm.Configuration;

public interface Module {

	void prepare(Configuration cnf);
	
	// cnf might not get used from every module 
	// but is provided if needed
	Instance<?> build(Configuration cnf);
	
	public interface Instance <ID extends Serializable>{
		boolean connect(Configuration cnf);
		boolean disconnect();
		boolean isConnected();
		
		Map<ID, String> query(String qry);
		boolean update(String qry);
	}
}
