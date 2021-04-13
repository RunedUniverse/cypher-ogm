package net.runeduniverse.libs.rogm.modules.neo4j;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.Session;
import net.runeduniverse.libs.rogm.logging.DebugLogger;
import net.runeduniverse.libs.rogm.modules.neo4j.Neo4jConfiguration;
import net.runeduniverse.libs.rogm.modules.neo4j.SessionTest;
import net.runeduniverse.libs.rogm.querying.IParameterized;
import net.runeduniverse.libs.rogm.test.ATest;
import net.runeduniverse.libs.rogm.test.LogLevelRule;
import net.runeduniverse.libs.rogm.test.model.*;
import net.runeduniverse.libs.rogm.test.model.relations.*;

public class SessionTest extends ATest {

	@ClassRule
	public static final LogLevelRule LOG_LEVEL_RULE = new LogLevelRule(SessionTest.class, Level.ALL);

	static Configuration config = new Neo4jConfiguration("127.0.0.1");
	static {
		config.setLogger(new DebugLogger(Logger.getLogger(SessionTest.class.getName())));

		config.addPackage(MODEL_PKG_PATH);
		config.addPackage(RELATIONS_PKG_PATH);

		config.setUser("neo4j");
		config.setPassword("Qwerty!");
	}

	public SessionTest() {
		super(config);
	}

	private Session session = null;

	@Before
	public void prepare() throws Exception {
		assertEquals("bolt", config.getProtocol());
		assertEquals(7687, config.getPort());
		assertEquals("127.0.0.1", config.getUri());

		this.session = Session.create(config);
		assertTrue("Session is NOT connected", session.isConnected());
	}

	@Test
	public void loadAllPeople() {
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
	public void loadAllArtists() {
		Collection<Artist> people = session.loadAll(Artist.class);
		if (people.isEmpty()) {
			System.out.println("NO ARTIST FOUND");
			return;
		}
		for (Artist artist : people) {
			System.out.println(artist.toString());
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void updatePerson() throws Exception {
		IParameterized personFilter = (IParameterized) session.getPattern(Person.class)
				.search(false);
		personFilter.getParams()
				.put("firstName", "Shawn");
		personFilter.getParams()
				.put("lastName", "James");

		Person shawn = session.load(Person.class, personFilter);
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
		System.out.println(james.toString());
		session.save(james);
		System.out.println(james.toString());
	}

	@Test
	public void createArtist() {
		Artist ennio = new Artist();
		ennio.setFirstName("Ennio");
		ennio.setLastName("Morricone");
		Song s = new Song("C’era una volta il West");
		ennio.getCreated()
				.add(s);
		ennio.getPlayed()
				.add(s);
		System.out.println(ennio.toString());
		session.save(ennio);
		System.out.println(ennio.toString());
	}

	@Test
	public void saveAndLoadPlayer_UUID_Id() {
		Player player = new Player(UUID.randomUUID(), "Testi", new Inventory());
		System.out.println(player.toString());
		session.save(player);
		session.unload(player);
		Player player2 = session.load(Player.class, player.getUuid());
		System.out.println(player2.toString());
		assertNotNull("NO Entries in DB found", player2);
		assertTrue("Player UUID doesn't match", player.getUuid()
				.equals(player2.getUuid()));
		assertFalse("Player did not get unloaded", player == player2);
	}

	@Test
	public void createAndDeletePlayer() {
		Player player = new Player(UUID.randomUUID(), "DUMMY PLAYER", new Inventory());
		session.save(player);
		session.delete(player);
	}

	@Test
	public void createAndDeleteEnnio() {
		Artist ennio = new Artist();
		ennio.setFirstName("Ennio");
		ennio.setLastName("Morricone");
		Song s = new Song("C’era una volta il West");
		ennio.getCreated()
				.add(s);
		ennio.getPlayed()
				.add(s);
		session.save(ennio);
		session.delete(ennio);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void loadCompany() throws Exception {
		IParameterized gameFilter = (IParameterized) session.getPattern(Company.class)
				.search(false);
		gameFilter.getParams()
				.put("name", "Naughty Dog");

		Company company = session.load(Company.class, gameFilter);
		Game game = new Game();
		game.setName("just another USELESS title");
		company.getGames()
				.add(game);
		session.save(company);
		company.getGames()
				.remove(game);
		session.save(company, 4);
		session.delete(game);
	}

	@Test
	public void loadActors() {
		Collection<Actor> actors = session.loadAllLazy(Actor.class);
		session.resolveAllLazyLoaded(actors, 3);
		for (Actor actor : actors)
			for (ActorPlaysPersonRelation rel : actor.getPlays())
				System.out.println("Actor: " + rel.getActor()
						.getFirstName() + " plays "
						+ rel.getPerson()
								.getFirstName());
	}

	@Test
	public void savePlayer() {
		Player player = new Player();
		player.setName("INV TEST PLAYER");
		player.setUuid(UUID.randomUUID());

		Inventory inv = new Inventory();
		inv.setSize(27);
		player.setInventory(inv);

		Item item = new Item();
		item.setItemStack("SAND");
		inv.getSlots()
				.add(new Slot(22, inv, item));

		session.save(player, 3);
	}

	/*
	 * Outdated until Advanced Filter full implementation
	 * 
	 * @Test public void bufferTest() { Actor ashley0 = session.load(Actor.class,
	 * 11L); Person ashley1 = session.load(Person.class, 11L); assertEquals(ashley1,
	 * ashley0); assertTrue(ashley0 == ashley1); }
	 */

	@After
	public void close() throws Exception {
		if (session != null)
			session.close();
	}
}
