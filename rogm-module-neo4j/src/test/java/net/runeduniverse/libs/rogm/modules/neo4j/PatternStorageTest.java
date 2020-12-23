package net.runeduniverse.libs.rogm.modules.neo4j;

import org.junit.*;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.modules.neo4j.Neo4jConfiguration;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;
import net.runeduniverse.libs.rogm.test.ATest;
import net.runeduniverse.libs.rogm.test.model.*;

public class PatternStorageTest extends ATest {

	static Configuration config = new Neo4jConfiguration("runeduniverse.net");
	static {
		config.addPackage(MODEL_PKG_PATH);
		config.addPackage(RELATIONS_PKG_PATH);
	}

	public PatternStorageTest() {
		super(config);
	}

	private PatternStorage storage = null;

	private static final Person testi;
	private static final Artist ennio;

	static {
		testi = new Person("Testi", "West", true);

		ennio = new Artist();
		ennio.setFirstName("Ennio");
		ennio.setLastName("Morricone");
		Song s = new Song("C’era una volta il West");
		ennio.getCreated().add(s);
		ennio.getPlayed().add(s);
	}

	@Before
	public void before() throws Exception {
		storage = new PatternStorage(config, iParser);
	}

	@Test
	public void queryCompany() throws Exception {
		System.out.println(_query(Company.class));
	}

	@Test
	public void queryActor() throws Exception {
		System.out.println(_query(Actor.class));
	}

	@Test
	public void queryHouse() throws Exception {
		System.out.println(_query(House.class));
	}

	@Test
	public void queryArtist() throws Exception {
		System.out.println(_query(Artist.class));
	}

	@Test
	public void savePerson() throws Exception {
		System.out.println(_save(testi));
	}

	@Test
	public void saveArtist() throws Exception {
		System.out.println(_save(ennio));
	}

	private String _query(Class<?> clazz) throws Exception {
		return "[QUERY][" + clazz.getSimpleName() + "]\n" + iLanguage.load(this.storage.getNode(clazz).search(false))
				+ '\n';
	}

	private String _save(Object entity) throws Exception {
		return "[SAVE][" + entity.getClass().getSimpleName() + "]\n"
				+ iLanguage.save(this.storage.save(entity, 1).getDataContainer(), null).qry() + '\n';
	}
}
