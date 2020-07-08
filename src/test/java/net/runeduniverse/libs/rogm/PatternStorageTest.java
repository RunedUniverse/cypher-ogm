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

	@Before
	public void before() {
		List<String> pkgs = new ArrayList<>();
		pkgs.add("net.runeduniverse.libs.rogm.model");
		pkgs.add("net.runeduniverse.libs.rogm.model.relations");
		storage = new PatternStorage(pkgs, module, parser);
	}

	@Test
	public void testCompany() throws Exception {
		System.out.println(_build(Company.class));
	}

	@Test
	public void testActor() throws Exception {
		System.out.println(_build(Actor.class));
	}

	@Test
	public void testHouse() throws Exception {
		System.out.println(_build(House.class));
	}

	private String _build(Class<?> clazz) throws Exception {
		return '['+clazz.getSimpleName() + "]\n" + lang.buildQuery(this.storage.getNode(clazz).createFilter(), parser)
				+ '\n';
	}

}
