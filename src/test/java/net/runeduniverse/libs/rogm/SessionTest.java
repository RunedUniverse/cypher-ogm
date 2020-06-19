package net.runeduniverse.libs.rogm;

import static org.junit.Assert.*;

import java.util.Collection;
import org.junit.Test;

import net.runeduniverse.libs.rogm.model.Person;

public class SessionTest {

	@Test
	public void buildSessionTest(){
		Configuration config = new Configuration(DatabaseType.Neo4j, "runeduniverse.net");
		
		config.setUser("neo4j");
		config.setPassword("Qwerty!");
		
		assertEquals("bolt", config.getProtocol());
		assertEquals(7687, config.getPort());
		assertEquals("runeduniverse.net", config.getUri());
		
		Session session = Session.create(config);
		
		assertTrue(session.isConnected());
		
		
		Collection<Person> people = session.loadAll(Person.class);
		if(people.isEmpty())
			System.out.println("NO PEOPLE FOUND");
		for (Person person : people) {
			System.out.println(person.toString());
		}
	}
	
	@Test
	public void neo4jQryTest() {
		
	}
}
