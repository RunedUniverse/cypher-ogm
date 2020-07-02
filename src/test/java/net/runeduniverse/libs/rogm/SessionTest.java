package net.runeduniverse.libs.rogm;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.runeduniverse.libs.rogm.model.Person;

public class SessionTest {

	static Configuration config = new Configuration(DatabaseType.Neo4j, "runeduniverse.net");
	static {
		config.setUser("neo4j");
		config.setPassword("Qwerty!");
		
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
		if(people.isEmpty())
			System.out.println("NO PEOPLE FOUND");
		for (Person person : people) {
			System.out.println(person.toString());
		}
	}
	
	@Test
	public void updatePerson() {
		assertTrue("Session is NOT connected", session.isConnected());
		Person shawn = session.load(Person.class, 23L);
		System.out.println(shawn.toString());
		shawn.setFictional(false);
		session.save(shawn);
	}
	
	@After
	public void close() throws Exception {
		session.close();
	}
}
