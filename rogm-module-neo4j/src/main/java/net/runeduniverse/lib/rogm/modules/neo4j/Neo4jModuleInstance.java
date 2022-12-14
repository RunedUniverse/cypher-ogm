/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.rogm.modules.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import lombok.RequiredArgsConstructor;
import net.runeduniverse.lib.rogm.api.info.ConnectionInfo;
import net.runeduniverse.lib.rogm.api.modules.IRawDataRecord;
import net.runeduniverse.lib.rogm.api.modules.IRawIdRecord;
import net.runeduniverse.lib.rogm.api.modules.IRawRecord;
import net.runeduniverse.lib.rogm.api.modules.Module;
import net.runeduniverse.lib.rogm.api.parser.Parser;
import net.runeduniverse.lib.rogm.modules.RawDataRecord;
import net.runeduniverse.lib.rogm.modules.RawIdRecord;
import net.runeduniverse.lib.rogm.modules.RawRecord;
import net.runeduniverse.lib.utils.logging.UniversalLogger;

public class Neo4jModuleInstance implements Module.Instance<Long> {
	private Driver driver = null;
	private Parser.Instance parser = null;
	private UniversalLogger logger = null;

	protected Neo4jModuleInstance(Parser.Instance parser, Logger logger) {
		this.parser = parser;
		this.logger = new UniversalLogger(Neo4jModuleInstance.class, logger);
	}

	@Override
	public boolean connect(ConnectionInfo info) {
		this.driver = GraphDatabase.driver(Neo4jModule.buildUri(info),
				AuthTokens.basic(info.getUser(), info.getPassword()));
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
			this.logger.burying("isConnected()", e);
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
					return tx.run(qry)
							.list();
				}
			});
		} catch (Exception e) {
			this.logger.burying("_query(String)", e);
		}
		return new ArrayList<Record>();
	}

	@Override
	public IRawRecord query(String qry) {
		RawRecord rawRecord = new RawRecord();
		for (Record record : _query(qry))
			rawRecord.addEntry(record.asMap());
		return rawRecord;
	}

	@Override
	public IRawDataRecord queryObject(String qry) {
		RawDataRecord dataRecord = new RawDataRecord();

		try {
			for (Record record : _query(qry)) {
				Map<String, Module.Data> data = new HashMap<>();
				for (String key : record.keys()) {
					if (key.startsWith("id_") || key.startsWith("eid_") || key.startsWith("labels_"))
						continue;
					data.put(key, new Data(this.parser, record, key));
				}
				dataRecord.addEntry(data);
			}
		} catch (Exception e) {
			this.logger.burying("queryObject(String)", e);
		}

		return dataRecord;
	}

	@Override
	public IRawIdRecord execute(String qry) {
		// -1 -> not found
		this.logger.finest("[[EXECUTE]]\n" + qry);
		try (Session session = driver.session()) {
			return session.writeTransaction(new TransactionWork<IRawIdRecord>() {

				@Override
				public IRawIdRecord execute(Transaction tx) {
					Result result = tx.run(qry);
					RawIdRecord idRecord = new RawIdRecord();
					if (!result.hasNext())
						return idRecord;
					Record record = result.next();
					List<String> keys = record.keys();
					List<Thread> threads = new ArrayList<>();

					for (int i = 0; i <= (int) keys.size() / Processor.BATCH_SIZE; i++)
						threads.add(new Processor(record, i, keys, idRecord).runAsThread());
					for (Thread t : threads)
						try {
							t.join();
						} catch (InterruptedException e) {
							Neo4jModuleInstance.this.logger.burying("execute(String)", e);
						}
					return idRecord;
				}
			});
		}
	}

	@RequiredArgsConstructor
	private class Processor implements Runnable {
		final static int BATCH_SIZE = 1000;

		private final Record record;
		private final Integer batch;
		private final List<String> keys;
		private final RawIdRecord idRecord;

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
						idRecord.put(key, null);
					else
						idRecord.put(key, record.get(key)
								.asString());

				} else
					idRecord.put(key, record.get(key, -1L));
			}
		}

		public Thread runAsThread() {
			Thread t = new Thread(this);
			t.start();
			return t;
		}
	}
}
