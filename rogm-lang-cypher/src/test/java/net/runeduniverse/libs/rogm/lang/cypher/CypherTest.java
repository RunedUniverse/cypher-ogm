/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.libs.rogm.lang.cypher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.parser.json.Feature;
import net.runeduniverse.libs.rogm.parser.json.JSONParser;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.FilterRelation;
import net.runeduniverse.libs.rogm.test.AConfigTest;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;

public class CypherTest extends AConfigTest {

	/*
	 * ASSERTS NOT VIABLE BECAUSE IT SOMEWHAT CHANGES THE DIRECTIONS INSIDE THE
	 * QUERIES !! THE QUERIES MUST BE CHECKED MANUALLY!!
	 */

	public CypherTest() {
		super(new Configuration(new JSONParser().configure(Feature.SERIALIZE_NULL_AS_EMPTY_OBJECT, true)
				.configure(Feature.SERIALIZER_QUOTE_FIELD_NAMES, false)
				.configure(Feature.DESERIALIZER_ALLOW_UNQUOTED_FIELD_NAMES, true)
				.configure(Feature.MAPPER_AUTO_DETECT_GETTERS, false)
				.configure(Feature.MAPPER_AUTO_DETECT_IS_GETTERS, false), new CypherLanguage(), new DummyModule(),
				"localhost"));
	}

	static FilterNode school;
	static FilterNode student;
	static FilterNode friends;
	static FilterRelation anyRelationToSchool;
	static FilterNode city = new FilterNode();

	@BeforeAll
	public static void prepare() {
		school = new FilterNode(10);

		student = new FilterNode().addLabel("HTLStudent")
				.addLabel("Maturant")
				.addRelationTo(school);

		friends = new FilterNode().addLabel("Person")
				.addRelation(new FilterRelation().addLabel("Friend"), student);

		anyRelationToSchool = new FilterRelation(Direction.BIDIRECTIONAL).setStart(school);

		Map<String, Object> infos = new HashMap<>();
		infos.put("Hospital", "The Royal London Hospital");
		infos.put("Parks", new String[] { "Hyde Park", "Greenwich Park", "Regent's Park" });
		city.addLabel("City")
				.addParam("name", "London")
				.addParam("Citizens", 9787426)
				.addParam("infos", infos);
	}

	@Test
	@Tag("system")
	public void testParserWithCypherConfig() throws Exception {
		String serial = iParser.serialize(null);
		assertFalse(serial.contains("null"), "null serialized as » " + serial + " « instead of » {} « or » «");
	}

	@Test
	@Tag("system")
	public void wrongID() {
		boolean error = false;
		try {
			iLanguage.load(new FilterNode("defaultId"));
		} catch (Exception e) {
			error = true;
		}
		assertTrue(error, "String is not a valid id");
	}

	@Test
	@Tag("system")
	public void shortID() throws Exception {
		iLanguage.load(new FilterNode((short) 3));
	}

	@Test
	@Tag("system")
	public void integerID() throws Exception {
		iLanguage.load(new FilterNode(45));
	}

	@Test
	@Tag("system")
	public void longID() throws Exception {
		iLanguage.load(new FilterNode(54l));
	}

	// MATCHES
	@Test
	@Tag("system")
	public void matchSchool() throws Exception {
		System.out.println("[SCHOOL]\n" + iLanguage.load(school) + '\n');
	}

	@Test
	@Tag("system")
	public void matchStudent() throws Exception {
		System.out.println("[STUDENT]\n" + iLanguage.load(student) + '\n');
	}

	@Test
	@Tag("system")
	public void matchFriends() throws Exception {
		System.out.println("[FRIENDS]\n" + iLanguage.load(friends) + '\n');
	}

	@Test
	@Tag("system")
	public void matchAnyRelationToSchool() throws Exception {
		System.out.println("[ANY REL]\n" + iLanguage.load(anyRelationToSchool) + '\n');
	}

	@Test
	@Tag("system")
	public void matchCity() throws Exception {
		System.out.println("[CITY]\n" + iLanguage.load(city) + '\n');
	}

	@Test
	@Tag("system")
	public void deleteEnnio() throws Exception {
		FilterNode ennio = new FilterNode(25L).setReturned(true);
		System.out.println("[ENNIO]\n" + iLanguage.delete(ennio,
				new FilterRelation().setDirection(Direction.BIDIRECTIONAL)
						.setStart(ennio)
						.setTarget(new FilterNode().setReturned(true))
						.setReturned(true))
				+ '\n');
	}

	@Test
	@Tag("system")
	public void deleteIDs() {
		CypherInstance instance = (CypherInstance) iLanguage;
		System.out
				.println("[DELETE IDs]\n" + instance.deleteRelations(Arrays.asList("10", "20", "514", "541", "5632")));
	}
}
