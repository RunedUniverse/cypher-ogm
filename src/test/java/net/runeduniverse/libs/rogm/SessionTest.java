package net.runeduniverse.libs.rogm;

import static org.junit.Assert.*;

import java.util.Collection;
import org.junit.Test;

import net.runeduniverse.libs.rogm.model.ShadowPlayer;

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
		
		
		Collection<ShadowPlayer> players = session.loadAll(ShadowPlayer.class);
		if(players.isEmpty())
			System.out.println("NO PLAYERS FOUND");
		for (ShadowPlayer player : players) {
			System.out.println(player.toString());
		}
	}
	
	@Test
	public void neo4jQryTest() {
		
	}
}
