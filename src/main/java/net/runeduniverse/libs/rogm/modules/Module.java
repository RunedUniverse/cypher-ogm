package net.runeduniverse.libs.rogm.modules;

import net.runeduniverse.libs.rogm.Configuration;

public interface Module {

	void prepare(Configuration cnf);
	
	boolean connect(Configuration cnf);
	boolean disconnect();
	
	String query(String qry);
	boolean update(String qry);
}
