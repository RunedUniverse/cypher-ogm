package net.runeduniverse.libs.rogm.test.system;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.logging.Logger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.logging.DebugLogger;
import net.runeduniverse.libs.rogm.modules.PassiveModule;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.RelationQueryBuilder;
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
		QueryBuilder.CREATOR_NODE_BUILDER = a -> new DebugNodeQueryBuilder(a);
		QueryBuilder.CREATOR_REALATION_BUILDER = a -> new DebugRelationQueryBuilder(a);
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

		this.archive = new Archive(this.cnf.getPackageInfo(), this.cnf.getModule());
		for (PassiveModule module : this.cnf.getPassiveModules())
			module.configure(archive);
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
		DebugRelationQueryBuilder slotRelQryBuilder = (DebugRelationQueryBuilder) builder.relation()
				.where(Slot.class)
				.whereParam("slot", 0);
		DebugNodeQueryBuilder itemNodeQryBuilder = (DebugNodeQueryBuilder) builder.node()
				.where(Item.class);
		DebugNodeQueryBuilder inventoryNodeQryBuilder = (DebugNodeQueryBuilder) builder.node()
				.where(Inventory.class)
				.addRelationTo(/* relation: */
						slotRelQryBuilder,
						/* target: */
						itemNodeQryBuilder)
				.asRead();
		// check Builder
		// assert connected Object-References
		assertFalse(inventoryNodeQryBuilder.getRelationBuilders()
				.isEmpty(), "NodeQueryBuilder: Inventory.relations is empty");
		assertFalse(itemNodeQryBuilder.getRelationBuilders()
				.isEmpty(), "NodeQueryBuilder: Item.relations is empty");
		assertTrue(inventoryNodeQryBuilder.getRelationBuilders()
				.contains(slotRelQryBuilder), "NodeQueryBuilder: Inventory.relations is missing ref to Slot");
		assertTrue(itemNodeQryBuilder.getRelationBuilders()
				.contains(slotRelQryBuilder), "NodeQueryBuilder: Item.relations is missing ref to Slot");
		assertNotNull(slotRelQryBuilder.getStart(), "NodeQueryBuilder: Slot.start == null");
		assertTrue(inventoryNodeQryBuilder == slotRelQryBuilder.getStart(),
				"NodeQueryBuilder: Slot.start != inventory");
		assertNotNull(slotRelQryBuilder.getTarget(), "NodeQueryBuilder: Slot.target == null");
		assertTrue(itemNodeQryBuilder == slotRelQryBuilder.getTarget(), "NodeQueryBuilder: Slot.target != item");

		// get filter interfaces
		IFNode inventory = inventoryNodeQryBuilder.getResult();
		IFRelation slot = slotRelQryBuilder.getResult();
		IFNode item = itemNodeQryBuilder.getResult();
		// check for null in results
		assertNotNull(inventory, "Inventory is null");
		assertNotNull(slot, "Slot is null");
		assertNotNull(item, "Item is null");
		// assert connected Object-References
		assertFalse(inventory.getRelations()
				.isEmpty(), "Inventory.relations is empty");
		assertFalse(item.getRelations()
				.isEmpty(), "Item.relations is empty");
		assertTrue(inventory.getRelations()
				.contains(slot), "Inventory.relations is missing ref to Slot");
		assertTrue(item.getRelations()
				.contains(slot), "Item.relations is missing ref to Slot");
		assertNotNull(slot.getStart(), "Slot.start == null");
		assertTrue(inventory == slot.getStart(), "Slot.start != inventory");
		assertNotNull(slot.getTarget(), "Slot.target == null");
		assertTrue(item == slot.getTarget(), "Slot.target != item");
	}

	private static class DebugNodeQueryBuilder extends NodeQueryBuilder {
		public DebugNodeQueryBuilder(Archive archive) {
			super(archive);
		}

		public Set<RelationQueryBuilder> getRelationBuilders() {
			return super.relationBuilders;
		}
	}

	private static class DebugRelationQueryBuilder extends RelationQueryBuilder {
		public DebugRelationQueryBuilder(Archive archive) {
			super(archive);
		}

		public NodeQueryBuilder getStart() {
			return super.startNodeBuilder;
		}

		public NodeQueryBuilder getTarget() {
			return super.targetNodeBuilder;
		}
	}
}
