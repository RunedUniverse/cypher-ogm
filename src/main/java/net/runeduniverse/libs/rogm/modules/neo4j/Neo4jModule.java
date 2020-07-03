package net.runeduniverse.libs.rogm.modules.neo4j;

import java.util.ArrayList;
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
import net.runeduniverse.libs.rogm.util.DataHashMap;
import net.runeduniverse.libs.rogm.util.DataMap;

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

		private List<Record> _query(String qry) {
			try (Session session = driver.session()) {
				return session.readTransaction(new TransactionWork<List<Record>>() {

					@Override
					public List<Record> execute(Transaction tx) {
						return tx.run(qry).list();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new ArrayList<Record>();
		}

		@Override
		public List<Map<String, Object>> query(String qry) {
			List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
			for (Record record : _query(qry))
				lst.add(record.asMap());
			return lst;
		}

		@Override
		public DataMap<Long, String, String> queryObject(String qry) {
			DataMap<Long, String, String> qryResults = new DataHashMap<>();

			try {
				for (Record record : _query(qry))
					for (String key : record.keys()) {
						if (key.startsWith("id_"))
							continue;
						qryResults.put(record.get("id_" + key).asLong(), this.parser.serialize(record.get(key).asMap()),
								key);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return qryResults;
		}

		@Override
		public Map<String, Long> execute(String qry) {
			// -1 -> not found

			try (Session session = driver.session()) {
				return session.writeTransaction(new TransactionWork<Map<String, Long>>() {

					@Override
					public Map<String, Long> execute(Transaction tx) {
						Map<String, Long> results = new HashMap<>();
						Record record = tx.run(qry).next();
						record.keys().forEach(key -> {
							results.put(key, record.get(key, -1L));
						});
						return results;
					}
				});
			}
		}
	}
}
