package net.runeduniverse.libs.rogm.modules.neo4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Value;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;

public class Neo4jModule implements Module {

	private static final String ID_ALIAS = "_id";

	@Override
	public void prepare(Configuration cnf) {
		cnf.setProtocol("bolt");
		cnf.setPort(7687);
	}

	@Override
	public Instance<Long> build(Configuration cnf) {
		return new Neo4jModuleInstance(cnf.getDbType().getParser().build(cnf), cnf.getLogger());
	}

	protected String _buildUri(Configuration cnf) {
		return cnf.getProtocol() + "://" + cnf.getUri() + ':' + cnf.getPort();
	}

	@Override
	public Class<?> idType() {
		return Long.class;
	}

	@Override
	public boolean checkIdType(Class<?> type) {
		if (type == null)
			return false;
		return Number.class.isAssignableFrom(type);
	}

	public String getIdAlias() {
		return ID_ALIAS;
	}

	public class Neo4jModuleInstance implements Module.Instance<Long> {
		private Driver driver = null;
		private Parser.Instance parser = null;
		private UniversalLogger logger = null;

		protected Neo4jModuleInstance(Parser.Instance parser, Logger logger) {
			this.parser = parser;
			this.logger = new UniversalLogger(Neo4jModuleInstance.class, logger);
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
			this.logger.finest("[[QUERY]]\n" + qry);
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
		public List<Map<String, Module.Data>> queryObject(String qry) {
			List<Map<String, Module.Data>> qryResults = new ArrayList<>();

			try {
				for (Record record : _query(qry)) {
					Map<String, Module.Data> data = new HashMap<>();
					for (String key : record.keys()) {
						if (key.startsWith("id_") || key.startsWith("eid_") || key.startsWith("labels_"))
							continue;
						data.put(key, new Data(this.parser, record, key));
					}
					qryResults.add(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return qryResults;
		}

		@Override
		public Map<String, Serializable> execute(String qry) {
			// -1 -> not found
			this.logger.finest("[[EXECUTE]]\n" + qry);
			try (Session session = driver.session()) {
				return session.writeTransaction(new TransactionWork<Map<String, Serializable>>() {

					@Override
					public Map<String, Serializable> execute(Transaction tx) {
						Result result = tx.run(qry);
						if (!result.hasNext())
							return new HashMap<>();
						Map<String, Serializable> results = new HashMap<>();
						Record record = result.next();
						List<String> keys = record.keys();
						List<Thread> threads = new ArrayList<>();

						for (int i = 0; i <= (int) keys.size() / Processor.BATCH_SIZE; i++)
							threads.add(new Processor(record, i, keys, results).runAsThread());
						for (Thread t : threads)
							try {
								t.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						return results;
					}
				});
			}
		}
	}

	@RequiredArgsConstructor
	private class Processor implements Runnable {
		final static int BATCH_SIZE = 1000;

		private final Record record;
		private final Integer batch;
		private final List<String> keys;
		private final Map<String, Serializable> results;

		@Override
		public void run() {
			int j;
			for (int i = 0; i < BATCH_SIZE; i++) {
				j = batch * BATCH_SIZE + i;
				if (j >= keys.size())
					return;
				String key = keys.get(j);
				if (key.charAt(0) == 'e') {
					Value value = record.get(key);
					if (value.isNull())
						results.put(key, null);
					else
						results.put(key, record.get(key).asString());

				} else
					results.put(key, record.get(key, -1L));
			}
		}

		public Thread runAsThread() {
			Thread t = new Thread(this);
			t.start();
			return t;
		}
	}

	@Getter
	public class Data implements Module.Data {

		private Long id;
		private String entityId;
		private Set<String> labels = new HashSet<>();
		private String data;
		private String alias;

		protected Data(Parser.Instance parser, Record record, String key) throws Exception {
			this.alias = key;
			Value idProperty = record.get("id_" + key);
			if (idProperty.isNull())
				return;
			this.id = record.get("id_" + key).asLong();

			Value eidProperty = record.get("eid_" + key);
			if (eidProperty.isNull())
				this.entityId = null;
			else
				this.entityId = eidProperty.asString();

			if (record.get(key).isNull())
				this.data = parser.serialize(null);
			else
				this.data = parser.serialize(record.get(key).asMap());

			Value labelsProperty = record.get("labels_" + key);
			if (List.class.isAssignableFrom(labelsProperty.asObject().getClass()))
				for (Object o : record.get("labels_" + key).asList())
					this.labels.add((String) o);
			else
				this.labels.add(labelsProperty.asString());
		}
	}
}
