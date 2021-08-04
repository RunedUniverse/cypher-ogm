package net.runeduniverse.libs.rogm.modules.neo4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.EntitiyFactory;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.rogm.test.ATest;
import net.runeduniverse.libs.rogm.test.ConsoleLogger;
import net.runeduniverse.libs.rogm.test.model.*;

public class PatternStorageTest extends ATest {

	static Configuration config = new Neo4jConfiguration("runeduniverse.net");
	static {
		config.addPackage(MODEL_PKG_PATH);
		config.addPackage(RELATIONS_PKG_PATH);

		Archive.PACKAGE_SCANNER_DEBUG_MODE = true;
	}

	public PatternStorageTest() {
		super(config);
	}

	private EntitiyFactory processor = null;
	private QueryBuilder qryBuilder = null;

	private static final Person testi;
	private static final Artist ennio;

	static {
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

	@BeforeEach
	public void before() throws Exception {
		this.processor = new EntitiyFactory(config, iParser);
		this.qryBuilder = this.processor.getArchive()
				.getQueryBuilder();
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
		return "[QUERY][" + clazz.getSimpleName() + "]\n" + iLanguage.load(this.qryBuilder.node()
				.where(clazz)
				.setLazy(false)
				.getResult()) + '\n';
	}

	private String _save(Object entity) throws Exception {
		this.processor.getArchive()
				.logPatterns(new ConsoleLogger());
		return "[SAVE][" + entity.getClass()
				.getSimpleName() + "]\n"
				+ iLanguage.save(this.processor.save(entity, 1)
						.getDataContainer(), null)
						.qry()
				+ '\n';
	}
}
