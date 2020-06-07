package net.runeduniverse.libs.rogm.modules.neo4j;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.modules.Module;

public class Neo4jModule implements Module{

	@Override
	public void prepare(Configuration cnf) {
		cnf.setProtocol("bolt");
		cnf.setPort(7687);
	}

	@Override
	public boolean connect(Configuration cnf) {
		return false;
	}

	@Override
	public boolean disconnect() {
		return false;
	}

	@Override
	public String query(String qry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update(String qry) {
		// TODO Auto-generated method stub
		return false;
	}

}
