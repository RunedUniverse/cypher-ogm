package net.runeduniverse.libs.rogm.test.model.relations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.Property;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.annotations.StartNode;
import net.runeduniverse.libs.rogm.annotations.TargetNode;
import net.runeduniverse.libs.rogm.test.model.Inventory;
import net.runeduniverse.libs.rogm.test.model.Item;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(label = "ITEM_SLOT", direction = Direction.OUTGOING)
public class Slot extends ARelationEntity {
	@Property
	private Integer slot;
	@StartNode
	private Inventory inventory;
	@TargetNode
	private Item item;
}
