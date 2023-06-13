/*
 * Copyright © 2023 VenaNocta (venanocta@gmail.com)
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

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.rogm.Configuration;
import net.runeduniverse.lib.rogm.test.AArchiveTest;
import net.runeduniverse.lib.rogm.test.ConsoleLogger;
import net.runeduniverse.lib.rogm.test.model.*;

public class Neo4jArchiveTest extends AArchiveTest {

	static Configuration config = new Neo4jConfiguration("runeduniverse.net");

	public Neo4jArchiveTest() {
		super(config, new ConsoleLogger(Logger.getLogger(Neo4jArchiveTest.class.getName())));
	}

	private static Person testi;
	private static Artist ennio;

	@BeforeAll
	public static void setup() {
		// set model packages
		config.addPackage(MODEL_PKG_PATH)
				.addPackage(RELATIONS_PKG_PATH);

		// define test objects
		testi = new Person("Testi", "West", true);

		ennio = new Artist();
		ennio.setFirstName("Ennio");
		ennio.setLastName("Morricone");
		Song s = new Song("C’era una volta il West");
		ennio.getCreated()
				.add(s);
		ennio.getPlayed()
				.add(s);
	}

	@Test
	@Tag("system")
	public void queryCompany() throws Exception {
		System.out.println(_query(Company.class));
	}

	@Test
	@Tag("system")
	public void queryActor() throws Exception {
		System.out.println(_query(Actor.class));
	}

	@Test
	@Tag("system")
	public void queryHouse() throws Exception {
		System.out.println(_query(House.class));
	}

	@Test
	@Tag("system")
	public void queryArtist() throws Exception {
		System.out.println(_query(Artist.class));
	}

	@Test
	@Tag("system")
	public void savePerson() throws Exception {
		System.out.println(_save(testi));
	}

	@Test
	@Tag("system")
	public void saveArtist() throws Exception {
		System.out.println(_save(ennio));
	}

	private String _query(Class<?> clazz) throws Exception {
		return super.printQuery(clazz, this.qryBuilder.node()
				.where(clazz)
				.setLazy(false)
				.getResult());
	}

	private String _save(Object entity) throws Exception {
		return super.printSave(entity, 1);
	}
}
