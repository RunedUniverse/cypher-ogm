package net.runeduniverse.libs.rogm;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.model.*;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;

public class PatternStorageTest {

	private static final DatabaseType dbType = DatabaseType.Neo4j;
	private static final Language lang = dbType.getLang();
	private static final Parser parser = dbType.getParser();
	private static final Module module = dbType.getModule();
	private PatternStorage storage = null;

	private static final Person testi;
	private static final Artist ennio;

	static {
		testi = new Person("Testi", "West", true);
		testi.setAddress(new Address("Sundown Road", 3));

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
		storage = new PatternStorage(pkgs, module, parser);
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
	public void savePerson() throws Exception {
		System.out.println(_save(testi));
	}

	@Test
	public void saveArtist() throws Exception {
		System.out.println(_save(ennio));
	}

	private String _query(Class<?> clazz) throws Exception {
		return "[QUERY][" + clazz.getSimpleName() + "]\n"
				+ lang.buildQuery(this.storage.getNode(clazz).createFilter(), parser) + '\n';
	}

	private String _save(Object entity) throws Exception {
		return "[SAVE][" + entity.getClass().getSimpleName() + "]\n"
				+ lang.buildSave(this.storage.createFilter(entity), parser).qry() + '\n';
	}
}
