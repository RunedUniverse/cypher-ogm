package net.runeduniverse.libs.rogm.parser;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import net.runeduniverse.libs.rogm.model.Address;
import net.runeduniverse.libs.rogm.model.City;
import net.runeduniverse.libs.rogm.model.House;
import net.runeduniverse.libs.rogm.model.Person;

public class JsonParserTest {

	private static final Parser parser = new JSONParser();

	private House h0;
	private House h1;
	private House h2;
	private City c;

	@Before
	public void prepare() {
		Person marry = new Person("Marry", "Log", true);
		Person frank = new Person("Frank", "Log", true);
		Person georg = new Person("Georg", "Baker", true);
		Person elma = new Person("Elma", "Light", true);
		Person luna = new Person("Luna", "Moon", true);

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
	public void parseCityTest() throws Exception {
		String s = "{name:\"Moonland\"," + "houses:["
				+ "{address:{street:\"Bakersstreet\",number:12},people:[{firstName:\"Marry\",lastName:\"Log\",fictional:true},{firstName:\"Frank\",lastName:\"Log\",fictional:true}]},"
				+ "{address:{street:\"Gardenstreet\",number:200},people:[{firstName:\"Georg\",lastName:\"Baker\",fictional:true},{firstName:\"Luna\",lastName:\"Moon\",fictional:true}]},"
				+ "{address:{street:\"Sundown Road\",number:3},people:[{firstName:\"Elma\",lastName:\"Light\",fictional:true}]}"
				+ "]}";
		assertEquals(s, parser.serialize(c));
	}

	@Test
	public void parseHouse() throws Exception {
		String s = "{address:{street:\"Bakersstreet\",number:12},people:[{firstName:\"Marry\",lastName:\"Log\",fictional:true},{firstName:\"Frank\",lastName:\"Log\",fictional:true}]}";
		assertEquals(s, parser.serialize(h0));
	}

	@Test
	public void parseHouseTransient() throws Exception {
		// ignores empty varialbe while parsing and sets it as default true
		// String serial =
		// "{address:{street:\"Gardenstreet\",number:200},people:[{firstName:\"Georg\",lastName:\"Baker\"},{firstName:\"Luna\",lastName:\"Moon\"}],
		// \"empty\":false}";
		String s = "{address:{street:\"Gardenstreet\",number:200},people:[{firstName:\"Georg\",lastName:\"Baker\",fictional:true},{firstName:\"Luna\",lastName:\"Moon\",fictional:true}], \"empty\":false}}";
		// Direct equals fails -> probably due to Sets
		assertEquals(h1.toString(), parser.deserialize(House.class, s).toString());
	}

}
