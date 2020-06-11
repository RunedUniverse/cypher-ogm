package net.runeduniverse.libs.rogm.modules.neo4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;

public class Neo4jModule implements Module {

	@Override
	public void prepare(Configuration cnf) {
		cnf.setProtocol("bolt");
		cnf.setPort(7687);
	}

	@Override
	public Instance<Long> build(Configuration cnf) {
		return new Neo4jModuleInstance(cnf.getDbType().getParser());
	}

	protected String _buildUri(Configuration cnf) {
		return cnf.getProtocol() + "://" + cnf.getUri() + ':' + cnf.getPort();
	}

	public class Neo4jModuleInstance implements Module.Instance<Long> {
		private Driver driver = null;
		private Parser parser = null;

		protected Neo4jModuleInstance(Parser parser) {
			this.parser = parser;
		}

		@Override
		public boolean connect(Configuration cnf) {
			this.driver = GraphDatabase.driver(_buildUri(cnf), AuthTokens.basic(cnf.getUser(), cnf.getPassword()));
			return isConnected();
		}

		@Override
		public boolean disconnect() {
			this.driver.close();
			return true;
		}

		@Override
		public boolean isConnected() {
			try {
				this.driver.verifyConnectivity();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		public Map<Long, String> query(String qry){
			Map<Long, String> qryResults = new HashMap<>();
			try (Session session = driver.session()) {
				for (Record record : session.readTransaction(new TransactionWork<List<Record>>() {

					@Override
					public List<Record> execute(Transaction tx) {
						return tx.run(qry).list();
					}
				}))
					qryResults.put(record.get(0).asLong(), this.parser.serialize(record.get(1).asMap()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return qryResults;
		}

		@Override
		public boolean update(String qry) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
