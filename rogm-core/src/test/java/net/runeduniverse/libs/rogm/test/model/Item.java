package net.runeduniverse.libs.rogm.test.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.PreSave;
import net.runeduniverse.libs.rogm.annotations.Property;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.annotations.Transient;

@Getter
@Setter
@ToString(callSuper = true)
public class Item extends AEntity {
	@Relationship(label = "CONTAINS_INVENTORY", direction = Direction.OUTGOING)
	private Inventory containingInventory;

	private String str = "my string";
	private Boolean bool = false;
	
	@Property
	private String itemStackData = null;
	
	@Transient
	@Getter
	private String itemStack = null;
	
	@PreSave
	private void _preSave() {
		this.itemStackData = "{type:"+itemStack+'}';
	}
}
