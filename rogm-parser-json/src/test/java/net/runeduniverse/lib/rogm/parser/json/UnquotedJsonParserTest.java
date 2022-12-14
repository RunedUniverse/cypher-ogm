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
package net.runeduniverse.lib.rogm.parser.json;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.rogm.api.Configuration;
import net.runeduniverse.lib.rogm.test.AConfigTest;
import net.runeduniverse.lib.rogm.test.dummies.DummyLanguage;
import net.runeduniverse.lib.rogm.test.dummies.DummyModule;
import net.runeduniverse.lib.rogm.test.model.*;

public class UnquotedJsonParserTest extends AConfigTest {

	public UnquotedJsonParserTest() {
		super(new Configuration(new JSONParser().configure(Feature.SERIALIZER_QUOTE_FIELD_NAMES, false)
				.configure(Feature.DESERIALIZER_ALLOW_UNQUOTED_FIELD_NAMES, true)
				.configure(Feature.MAPPER_AUTO_DETECT_GETTERS, false)
				.configure(Feature.MAPPER_AUTO_DETECT_IS_GETTERS, false), new DummyLanguage(), new DummyModule(), ""));
	}

	private static final House h0;
	private static final House h1;
	private static final House h2;
	private static final City c;
	private static final Person gray;

	static {
		Person marry = new Person("Marry", "Log", true);
		Person frank = new Person("Frank", "Log", true);
		Person georg = new Person("Georg", "Baker", true);
		Person elma = new Person("Elma", "Light", true);
		Person luna = new Person("Luna", "Moon", true);
		gray = new Person("Gray", "Baker", true);

		h0 = new House();
		h0.setAddress(new Address("Bakersstreet", 12));
		h0.setPeople(Arrays.asList(marry, frank));

		h1 = new House();
		h1.setAddress(new Address("Gardenstreet", 200));
		h1.setPeople(Arrays.asList(georg, luna));

		h2 = new House();
		h2.setAddress(new Address("Sundown Road", 3));
		h2.setPeople(Arrays.asList(elma));

		c = new City();
		c.setName("Moonland");
		c.setHouses(Arrays.asList(h0, h1, h2));
	}

	@Test
	@Tag("system")
	public void parseCityTest() throws Exception {
		String s = "{name:\"Moonland\"," + "houses:["
				+ "{address:{street:\"Bakersstreet\",number:12},people:[{firstName:\"Marry\",lastName:\"Log\",fictional:true},{firstName:\"Frank\",lastName:\"Log\",fictional:true}]},"
				+ "{address:{street:\"Gardenstreet\",number:200},people:[{firstName:\"Georg\",lastName:\"Baker\",fictional:true},{firstName:\"Luna\",lastName:\"Moon\",fictional:true}]},"
				+ "{address:{street:\"Sundown Road\",number:3},people:[{firstName:\"Elma\",lastName:\"Light\",fictional:true}]}"
				+ "]}";
		assertEquals(s, iParser.serialize(c));
	}

	@Test
	@Tag("system")
	public void parseHouse() throws Exception {
		String s = "{address:{street:\"Bakersstreet\",number:12},people:[{firstName:\"Marry\",lastName:\"Log\",fictional:true},{firstName:\"Frank\",lastName:\"Log\",fictional:true}]}";
		assertEquals(s, iParser.serialize(h0));
	}

	@Test
	@Tag("system")
	public void parseHouseTransient() throws Exception {
		// ignores empty varialbe while parsing and sets it as default true
		// String serial =
		// "{address:{street:\"Gardenstreet\",number:200},people:[{firstName:\"Georg\",lastName:\"Baker\"},{firstName:\"Luna\",lastName:\"Moon\"}],
		// \"empty\":false}";
		String s = "{address:{street:\"Gardenstreet\",number:200},people:[{firstName:\"Georg\",lastName:\"Baker\",fictional:true},{firstName:\"Luna\",lastName:\"Moon\",fictional:true}], \"empty\":false}}";
		// Direct equals fails -> probably due to Sets
		assertEquals(h1.toString(), iParser.deserialize(House.class, s)
				.toString());
	}

	@Test
	@Tag("system")
	public void parseEmpty() throws Exception {
		Person person = iParser.deserialize(Person.class, "{}");
		System.out.println("Person is " + person);
		assertNotNull(person, "Person {} is null");
	}

	@Test
	@Tag("system")
	public void serialNoGetter() throws Exception {
		String s = "{firstName:\"Gray\",lastName:\"Baker\",fictional:true}";
		assertEquals(s, iParser.serialize(gray));
	}

	@Test
	@Tag("system")
	public void ignoreNull() throws Exception {
		Item item = new Item();
		item.setStr(null);
		assertEquals("{bool:false}", iParser.serialize(item));
	}

}
