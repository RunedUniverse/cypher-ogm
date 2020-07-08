package net.runeduniverse.libs.rogm;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.runeduniverse.libs.rogm.model.Address;
import net.runeduniverse.libs.rogm.model.Artist;
import net.runeduniverse.libs.rogm.model.Person;

public class SessionTest {

	static Configuration config = new Configuration(DatabaseType.Neo4j, "runeduniverse.net");
	static {
		config.addPackage("net.runeduniverse.libs.rogm.model");
		config.addPackage("net.runeduniverse.libs.rogm.model.relations");
		
		config.setUser("neo4j");
		config.setPassword("Qwerty!");
		config.setPassword("t3fGGkgUbd7y8cJ8s5sUKBBDqkqDRLBw6Re8XbA2xaxpVe7Y7nQdZVj4mEsSHQnPXBWnsn7nFxtxKYTyge77HzMPtm3Jj7L45DYBK9Xy7fntrECnx5QMZWwFnUqCZ3JyN8d6LnZXnJbRxEkYD5rCpQhSpEtYz7DwQNA9Yd8T8RUuTduqrTCgvpCRZfHYhGbuKcHyR7QALXvQ9feSdX2ZhsvP8LmBzSh6s2TWLy37KatsYbrzQkCDpCE3zjyX9dzUd");
		
	}

	private Session session = null;

	@Before
	public void prepare() {
		assertEquals("bolt", config.getProtocol());
		assertEquals(7687, config.getPort());
		assertEquals("runeduniverse.net", config.getUri());

		this.session = Session.create(config);
	}

	@Test
	public void loadAllPeople() {
		assertTrue("Session is NOT connected", session.isConnected());
		Collection<Person> people = session.loadAll(Person.class);
		if (people.isEmpty()) {
			System.out.println("NO PEOPLE FOUND");
			return;
		}
		for (Person person : people) {
			System.out.println(person.toString());
		}
	}

	@Test
	public void updatePerson() {
		assertTrue("Session is NOT connected", session.isConnected());
		Person shawn = session.load(Person.class, 10L);
		System.out.println(shawn.toString());
		shawn.setFirstName("Shawn");
		shawn.setLastName("James");
		shawn.setFictional(false);
		session.save(shawn);
		System.out.println(shawn.toString());
	}

	@Test
	public void createPerson() {
		Person james = new Person("James", "North", true);
		//james.setAddress(new Address("Sundown Road", 3));
		System.out.println(james.toString());
		session.save(james);
		System.out.println(james.toString());
	}

	@Test
	public void bufferTest() {
		Artist ashley0 = session.load(Artist.class, 41L);
		Person ashley1 = session.load(Person.class, 41L);
		assertEquals(ashley1, ashley0);
		assertTrue(ashley0 == ashley1);
	}

	@After
	public void close() throws Exception {
		session.close();
	}
}
