package net.runeduniverse.libs.rogm;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import net.runeduniverse.libs.rogm.model.*;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;

public class PatternStorageTest extends ATest {

	public PatternStorageTest() {
		super(DatabaseType.Neo4j);
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
		List<String> pkgs = new ArrayList<>();
		pkgs.add("net.runeduniverse.libs.rogm.model");
		pkgs.add("net.runeduniverse.libs.rogm.model.relations");
		storage = new PatternStorage(pkgs, module, iParser);
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
		return "[QUERY][" + clazz.getSimpleName() + "]\n"
				+ iLanguage.buildQuery(this.storage.getNode(clazz).createFilter()) + '\n';
	}

	private String _save(Object entity) throws Exception {
		return "[SAVE][" + entity.getClass().getSimpleName() + "]\n"
				+ iLanguage.buildSave(this.storage.createFilter(entity).getDataFilter()).qry() + '\n';
	}
}
