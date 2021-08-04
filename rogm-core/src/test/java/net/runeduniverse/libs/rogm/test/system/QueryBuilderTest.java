package net.runeduniverse.libs.rogm.test.system;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Logger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.logging.DebugLogger;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.libs.rogm.test.ATest;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;
import net.runeduniverse.libs.rogm.test.model.Company;
import net.runeduniverse.libs.rogm.test.model.Inventory;
import net.runeduniverse.libs.rogm.test.model.Item;
import net.runeduniverse.libs.rogm.test.model.relations.Slot;

public class QueryBuilderTest {

	public static final String MODEL_PKG_PATH = ATest.MODEL_PKG_PATH;
	public static final String RELATIONS_PKG_PATH = ATest.RELATIONS_PKG_PATH;

	static {
		Archive.PACKAGE_SCANNER_DEBUG_MODE = true;
	}

	protected final Configuration cnf;
	protected final Archive archive;
	protected final QueryBuilder builder;

	public QueryBuilderTest() throws ScannerException {
		this.cnf = new Configuration(null, null, new DummyModule(), null).addClassLoader(this.getClass()
				.getClassLoader());
		cnf.setLogger(new DebugLogger(Logger.getLogger(QueryBuilderTest.class.getName())));

		cnf.addPackage(MODEL_PKG_PATH);
		cnf.addPackage(RELATIONS_PKG_PATH);

		this.archive = new Archive(this.cnf);
		this.archive.applyConfig();
		this.builder = this.archive.getQueryBuilder();
	}

	@Test
	@Tag("system")
	public void test() {
		builder.node()
				.where(Company.class);
	}

	@Test
	@Tag("system")
	public void nodeToNode() {
		// build with QueryBuilder
		NodeQueryBuilder invQryBuilder = builder.node()
				.where(Inventory.class)
				.addRelation(builder.relation()
						.where(Slot.class)
						.setTarget(builder.node()
								.where(Item.class)))
				.asRead();
		// get filter interfaces
		IFNode inventory = invQryBuilder.getResult();
		IFRelation slot = inventory.getRelations()
				.get(0);
		IFNode item = slot.getTarget();
		// assert connected Object-References
		assertTrue(inventory.getRelations()
				.contains(slot), "Inventory.relations is missing ref to Slot");
		assertTrue(item.getRelations()
				.contains(slot), "Item.relations is missing ref to Slot");
		assertNotNull(slot.getStart(), "Slot.start == null");
		assertTrue(inventory == slot.getStart(), "Slot.start != inventory");
		assertNotNull(slot.getTarget(), "Slot.target == null");
		assertTrue(item == slot.getTarget(), "Slot.target != item");
	}
}
