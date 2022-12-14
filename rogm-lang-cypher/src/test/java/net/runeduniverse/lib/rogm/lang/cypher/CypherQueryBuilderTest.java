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
package net.runeduniverse.lib.rogm.lang.cypher;

import java.util.Collections;
import java.util.logging.Logger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.rogm.api.Configuration;
import net.runeduniverse.lib.rogm.api.annotations.Direction;
import net.runeduniverse.lib.rogm.api.errors.ScannerException;
import net.runeduniverse.lib.rogm.api.querying.IDataContainer;
import net.runeduniverse.lib.rogm.api.querying.IFRelation;
import net.runeduniverse.lib.rogm.api.querying.IFilter;
import net.runeduniverse.lib.rogm.parser.json.Feature;
import net.runeduniverse.lib.rogm.parser.json.JSONParser;
import net.runeduniverse.lib.rogm.querying.FilterNode;
import net.runeduniverse.lib.rogm.querying.FilterRelation;
import net.runeduniverse.lib.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.lib.rogm.querying.QueryBuilder.RelationQueryBuilder;
import net.runeduniverse.lib.rogm.test.AQueryBuilderTest;
import net.runeduniverse.lib.rogm.test.ConsoleLogger;
import net.runeduniverse.lib.rogm.test.dummies.DummyModule;
import net.runeduniverse.lib.rogm.test.model.Artist;
import net.runeduniverse.lib.rogm.test.model.Song;
import net.runeduniverse.lib.rogm.test.system.TestEntity;

public class CypherQueryBuilderTest extends AQueryBuilderTest {

	public CypherQueryBuilderTest() throws ScannerException {
		super(new Configuration(new JSONParser().configure(Feature.SERIALIZE_NULL_AS_EMPTY_OBJECT, true)
				.configure(Feature.SERIALIZER_QUOTE_FIELD_NAMES, false)
				.configure(Feature.DESERIALIZER_ALLOW_UNQUOTED_FIELD_NAMES, true)
				.configure(Feature.MAPPER_AUTO_DETECT_GETTERS, false)
				.configure(Feature.MAPPER_AUTO_DETECT_IS_GETTERS, false), new CypherLanguage(), new DummyModule(),
				"localhost"), new ConsoleLogger(Logger.getLogger(CypherQueryBuilderTest.class.getName())));
	}

	@SuppressWarnings("unchecked")
	@Test
	@Tag("system")
	public void createEnnio() throws Exception {
		Artist ennio = new Artist();
		ennio.setFirstName("Ennio");
		ennio.setLastName("Morricone");
		Song s = new Song("C’era una volta il West");
		ennio.getCreated()
				.add(s);
		ennio.getPlayed()
				.add(s);

		NodeQueryBuilder ennioBuilder = this.qryBuilder.node()
				.setReturned(true)
				.where(Artist.class)
				.storeData(ennio)
				.asWrite();
		IFilter ennioFilter = ennioBuilder.getResult();
		TestEntity.infoTesting(this.logger, ennioBuilder);
		System.out.println("[ENNIO]\n" + iLanguage.save((IDataContainer) ennioFilter, Collections.EMPTY_SET) + '\n');
	}

	@Test
	@Tag("system")
	public void deleteEnnio() throws Exception {
		NodeQueryBuilder ennioBuilder = this.qryBuilder.node()
				.whereId(25L)
				.setReturned(true)
				.asDelete();
		RelationQueryBuilder relBuilder = this.qryBuilder.relation()
				.whereDirection(Direction.BIDIRECTIONAL)
				.setStart(ennioBuilder)
				.setTarget(this.qryBuilder.node()
						.setReturned(true));
		IFilter ennioFilter = ennioBuilder.getResult();
		IFRelation relFilter = relBuilder.getResult();
		TestEntity.infoTesting(this.logger, ennioBuilder);
		TestEntity.infoTesting(this.logger, relBuilder);
		System.out.println("[ENNIO]\n" + iLanguage.delete(ennioFilter, relFilter) + '\n');

		FilterNode ennio = new FilterNode(25L).setReturned(true);
		FilterRelation rel = new FilterRelation().setDirection(Direction.BIDIRECTIONAL)
				.setStart(ennio)
				.setTarget(new FilterNode().setReturned(true))
				.setReturned(true);
		TestEntity.infoTesting(this.logger, ennio);
		TestEntity.infoTesting(this.logger, rel);
		System.out.println("[ENNIO]\n" + iLanguage.delete(ennio, rel) + '\n');
	}
}
