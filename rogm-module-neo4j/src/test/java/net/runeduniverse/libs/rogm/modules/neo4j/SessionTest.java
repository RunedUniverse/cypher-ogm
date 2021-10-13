package net.runeduniverse.libs.rogm.modules.neo4j;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.Session;
import net.runeduniverse.libs.rogm.logging.DebugLogger;
import net.runeduniverse.libs.rogm.modules.neo4j.SessionTest;
import net.runeduniverse.libs.rogm.pipeline.Pipeline;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.test.AConfigTest;
import net.runeduniverse.libs.rogm.test.ConsoleLogger;
import net.runeduniverse.libs.rogm.test.model.*;
import net.runeduniverse.libs.rogm.test.model.relations.*;
import net.runeduniverse.libs.rogm.test.system.TestModelEntity;
import net.runeduniverse.libs.rogm.test.system.TestModelNode;

public class SessionTest extends AConfigTest {

	public static String DB_HOST = System.getProperty("dbhost", "127.0.0.1");
	public static String DB_USER = System.getProperty("dbuser", "neo4j");
	public static String DB_PW = System.getProperty("dbpw", "Qwerty!");

	private static Configuration config;
	private static Logger classLogger;

	private Pipeline pipeline = null;

	@BeforeAll
	public static void prepare() {
		config = new Neo4jConfiguration(DB_HOST);

		config.addClassLoader(SessionTest.class.getClassLoader())
				.addClassLoader(ClassLoader.getSystemClassLoader())
				.addClassLoader(Thread.currentThread()
						.getContextClassLoader());

		classLogger = new ConsoleLogger(Logger.getLogger(SessionTest.class.getName()));
		classLogger.setLevel(Level.ALL);
		config.setLogger(new DebugLogger(classLogger));

		config.addPackage(MODEL_PKG_PATH);
		config.addPackage(RELATIONS_PKG_PATH);

		config.setUser(DB_USER);
		config.setPassword(DB_PW);

		assertEquals("bolt", config.getProtocol());
		assertEquals(7687, config.getPort());
		assertEquals(DB_HOST, config.getUri());
	}

	public SessionTest() throws Exception {
		super(config);
		this.pipeline = new Pipeline(new DebugDatabasePipelineFactory(config));
	}

	@Test
	@Tag("system")
	public void debugLogTest() {
		classLogger.log(Level.ALL, "=== DEBUG LOG TEST ===");
	}

	private static void connectionCheck(Session session) {
		assertTrue(session.isConnected(), "Session is NOT connected");
	}

	@Test
	@Tag("db-neo4j")
	public void loadAllPeople() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			Collection<Person> people = session.loadAll(Person.class);
			assertNotEquals(0, people.size(), "no Artists loaded!");
			for (Person person : people) {
				TestModelEntity.infoTesting(classLogger, person);
				TestModelEntity.assertEntity(Person.class, person);
				TestModelNode.assertId(person);
			}
		}
	}

	@Test
	@Tag("db-neo4j")
	public void loadAllArtists() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			Collection<Artist> people = session.loadAll(Artist.class);
			assertNotEquals(0, people.size(), "no Artists loaded!");
			for (Artist person : people) {
				TestModelEntity.infoTesting(classLogger, person);
				TestModelEntity.assertEntity(Artist.class, person);
				TestModelNode.assertId(person);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	@Tag("db-neo4j")
	public void updatePerson() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			IFNode personFilter = session.getQueryBuilder()
					.node()
					.where(Person.class)
					.whereParam("firstName", "Shawn")
					.whereParam("lastName", "James")
					.getResult();

			Person shawn = session.load(personFilter);
			TestModelEntity.assertEntity(Person.class, shawn);
			TestModelNode.assertId(shawn);
			info("[Shawn]\n" + iLanguage.load(personFilter) + '\n');
			shawn.setFirstName("Shawn");
			shawn.setLastName("James");
			shawn.setFictional(false);
			session.save(shawn);
			info(shawn);
		}
	}

	@Test
	@Tag("db-neo4j")
	public void createPerson() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			Person james = new Person("James", "North", true);
			info(james);
			session.save(james);
			info(james);
			TestModelNode.assertId(james);
		}
	}

	@Test
	@Tag("db-neo4j")
	public void createArtist() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			Artist ennio = new Artist();
			ennio.setFirstName("Ennio");
			ennio.setLastName("Morricone");
			Song s = new Song("C’era una volta il West");
			ennio.getCreated()
					.add(s);
			ennio.getPlayed()
					.add(s);
			info(ennio);
			session.save(ennio);
			info(ennio);
			TestModelNode.assertId(ennio);
		}
	}

	@Test
	@Tag("db-neo4j")
	public void saveAndLoadPlayer_UUID_Id() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			Player player = new Player(UUID.randomUUID(), "Testi", new Inventory());
			info(player);
			session.save(player);
			session.unload(player);
			Player player2 = session.load(Player.class, player.getUuid());
			info(player2);
			assertNotNull(player2, "NO Entries in DB found");
			assertTrue(player.getUuid()
					.equals(player2.getUuid()), "Player UUID doesn't match");
			assertFalse(player == player2, "Player did not get unloaded");
		}
	}

	@Test
	@Tag("db-neo4j")
	public void createAndDeletePlayer() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			Player player = new Player(UUID.randomUUID(), "DUMMY PLAYER", new Inventory());
			session.save(player);
			session.delete(player);
		}
	}

	@Test
	@Tag("db-neo4j")
	public void createAndDeleteEnnio() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			Artist ennio = new Artist();
			ennio.setFirstName("Ennio");
			ennio.setLastName("Morricone");
			Song s = new Song("C’era una volta il West");
			ennio.getCreated()
					.add(s);
			ennio.getPlayed()
					.add(s);
			session.save(ennio);
			TestModelNode.assertId(ennio);
			session.delete(ennio);
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	@Tag("db-neo4j")
	public void loadCompany() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			IFNode companyFilter = session.getQueryBuilder()
					.node()
					.where(Company.class)
					.whereParam("name", "Naughty Dog")
					.getResult();

			Company company = session.load(companyFilter);
			TestModelEntity.assertEntity(Company.class, company);
			TestModelNode.assertId(company);
			Game game = new Game();
			game.setName("just another USELESS title");
			company.getGames()
					.add(game);
			session.save(company, 2);
			TestModelNode.assertId(game);
			company.getGames()
					.remove(game);
			session.save(company, 4);
			session.delete(game);
		}
	}

	@Test
	@Tag("db-neo4j")
	public void loadActorsAndResolveAllLazyLoaded() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
			Collection<Actor> actors = session.loadAllLazy(Actor.class);
			assertEquals(2, actors.size(), "wrong amount of Actors loaded!");
			session.resolveAllLazyLoaded(actors, 3);
			for (Actor actor : actors) {
				TestModelEntity.infoTesting(classLogger, actor);
				TestModelEntity.assertEntity(Actor.class, actor);
				assertNotNull(actor.getPlays(),
						"Actor.getPlays() = NULL, session.resolveAllLazyLoaded(actors, 3); probably failed!");
				TestModelNode.assertId(actor);
				for (ActorPlaysPersonRelation rel : actor.getPlays()) {
					TestModelEntity.assertEntity(ActorPlaysPersonRelation.class, rel);
					info("Actor: " + rel.getActor()
							.getFirstName() + " plays "
							+ rel.getPerson()
									.getFirstName());
				}
			}
		}
	}

	@Test
	@Tag("db-neo4j")
	public void savePlayer() throws Exception {
		try (Session session = this.pipeline.buildSession()) {
			connectionCheck(session);
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
	}

	/*
	 * Outdated until Advanced Filter full implementation
	 * 
	 * @Test public void bufferTest() { Actor ashley0 = session.load(Actor.class,
	 * 11L); Person ashley1 = session.load(Person.class, 11L); assertEquals(ashley1,
	 * ashley0); assertTrue(ashley0 == ashley1); }
	 */

	protected static void info(Object o) {
		classLogger.info(o == null ? "null" : o.toString());
	}
}
