package net.runeduniverse.libs.rogm.modules.neo4j;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.test.AArchiveTest;
import net.runeduniverse.libs.rogm.test.model.*;

public class Neo4jArchiveTest extends AArchiveTest {

	static Configuration config = new Neo4jConfiguration("runeduniverse.net");

	public Neo4jArchiveTest() {
		super(config);
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
