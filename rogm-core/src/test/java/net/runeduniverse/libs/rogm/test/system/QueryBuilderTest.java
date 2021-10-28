/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.libs.rogm.test.system;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Logger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.test.AQueryBuilderTest;
import net.runeduniverse.libs.rogm.test.ConsoleLogger;
import net.runeduniverse.libs.rogm.test.dummies.DummyLanguage;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;
import net.runeduniverse.libs.rogm.test.dummies.DummyParser;
import net.runeduniverse.libs.rogm.test.model.Company;
import net.runeduniverse.libs.rogm.test.model.Inventory;
import net.runeduniverse.libs.rogm.test.model.Item;
import net.runeduniverse.libs.rogm.test.model.relations.Slot;

public class QueryBuilderTest extends AQueryBuilderTest {

	public QueryBuilderTest() {
		super(new Configuration(new DummyParser(), new DummyLanguage(), new DummyModule(), "localhost"),
				new ConsoleLogger(Logger.getLogger(QueryBuilderTest.class.getName())));
	}

	@Test
	@Tag("system")
	public void test() {
		this.qryBuilder.node()
				.where(Company.class);
	}

	@Test
	@Tag("system")
	public void nodeToNode() {
		// build with QueryBuilder
		DebugRelationQueryBuilder slotRelQryBuilder = (DebugRelationQueryBuilder) qryBuilder.relation()
				.where(Slot.class)
				.whereParam("slot", 0);
		DebugNodeQueryBuilder itemNodeQryBuilder = (DebugNodeQueryBuilder) qryBuilder.node()
				.where(Item.class);
		DebugNodeQueryBuilder inventoryNodeQryBuilder = (DebugNodeQueryBuilder) qryBuilder.node()
				.where(Inventory.class)
				.addRelationTo(/* relation: */
						slotRelQryBuilder,
						/* target: */
						itemNodeQryBuilder)
				.asRead();
		// check Builder
		TestEntity.infoTesting(this.logger, inventoryNodeQryBuilder);
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
}
