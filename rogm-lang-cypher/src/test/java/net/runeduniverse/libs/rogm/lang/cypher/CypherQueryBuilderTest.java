package net.runeduniverse.libs.rogm.lang.cypher;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;
import java.util.logging.Logger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.parser.json.Feature;
import net.runeduniverse.libs.rogm.parser.json.JSONParser;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.FilterRelation;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.RelationQueryBuilder;
import net.runeduniverse.libs.rogm.test.AQueryBuilderTest;
import net.runeduniverse.libs.rogm.test.ConsoleLogger;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;
import net.runeduniverse.libs.rogm.test.model.Artist;
import net.runeduniverse.libs.rogm.test.model.Song;
import net.runeduniverse.libs.rogm.test.system.TestEntity;

public class CypherQueryBuilderTest extends AQueryBuilderTest {

	public CypherQueryBuilderTest() throws ScannerException {
		super(new Configuration(new JSONParser().configure(Feature.SERIALIZER_QUOTE_FIELD_NAMES, false)
				.configure(Feature.DESERIALIZER_ALLOW_UNQUOTED_FIELD_NAMES, true)
				.configure(Feature.MAPPER_AUTO_DETECT_GETTERS, false)
				.configure(Feature.MAPPER_AUTO_DETECT_IS_GETTERS, false), new CypherLanguage(), new DummyModule(),
				"localhost"), new ConsoleLogger(Logger.getLogger(CypherQueryBuilderTest.class.getName())));
	}

	@Test
	@Tag("system")
	public void testParserWithCypherConfig() throws Exception {
		this.archive.logPatterns(this.logger);
		String serial = iParser.serialize(null);
		assertFalse(serial.contains("null"), "null serialized as » " + serial + " « instead of » {} « or » «");
	}

	@SuppressWarnings("unchecked")
	@Test
	@Tag("system")
	public void createEnnio() throws Exception {
		Artist ennio = new Artist();
		ennio.setFirstName("Ennio");
		ennio.setLastName("Morricone");
		Song s = new Song("C’era una volta il West");
		ennio.getCreated()
				.add(s);
		ennio.getPlayed()
				.add(s);

		NodeQueryBuilder ennioBuilder = this.qryBuilder.node()
				.setReturned(true)
				.where(Artist.class)
				.storeData(ennio)
				.asWrite();
		IFilter ennioFilter = ennioBuilder.getResult();
		TestEntity.infoTesting(this.logger, ennioBuilder);
		System.out.println("[ENNIO]\n" + iLanguage.save((IDataContainer) ennioFilter, Collections.EMPTY_SET) + '\n');
	}

	@Test
	@Tag("system")
	public void deleteEnnio() throws Exception {
		NodeQueryBuilder ennioBuilder = this.qryBuilder.node()
				.whereId(25L)
				.setReturned(true)
				.asDelete();
		RelationQueryBuilder relBuilder = this.qryBuilder.relation()
				.whereDirection(Direction.BIDIRECTIONAL)
				.setStart(ennioBuilder)
				.setTarget(this.qryBuilder.node()
						.setReturned(true));
		IFilter ennioFilter = ennioBuilder.getResult();
		IFRelation relFilter = relBuilder.getResult();
		TestEntity.infoTesting(this.logger, ennioBuilder);
		TestEntity.infoTesting(this.logger, relBuilder);
		System.out.println("[ENNIO]\n" + iLanguage.delete(ennioFilter, relFilter) + '\n');

		FilterNode ennio = new FilterNode(25L).setReturned(true);
		FilterRelation rel = new FilterRelation().setDirection(Direction.BIDIRECTIONAL)
				.setStart(ennio)
				.setTarget(new FilterNode().setReturned(true))
				.setReturned(true);
		TestEntity.infoTesting(this.logger, ennio);
		TestEntity.infoTesting(this.logger, rel);
		System.out.println("[ENNIO]\n" + iLanguage.delete(ennio, rel) + '\n');
	}
}
