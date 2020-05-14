package net.runeduniverse.libs.rogm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.runeduniverse.libs.rogm.enums.DatabaseType;
import net.runeduniverse.libs.rogm.lang.Language;

public class SessionTest {

	@Test
	public void buildSessionTest(){
		System.out.println(DatabaseType.Neo4j.getLang() instanceof Language);
		
		
		
		Configuration config = new Configuration(DatabaseType.Neo4j);
		
		config.setUser("user");
		config.setPassword("Qwerty!");
		
		assertEquals("bolt", config.getProtocol());
		assertEquals(7687, config.getPort());
		
		Session session = Session.create(config);
		
		
	}
	
}
