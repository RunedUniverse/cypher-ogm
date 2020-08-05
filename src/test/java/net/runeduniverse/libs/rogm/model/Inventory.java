package net.runeduniverse.libs.rogm.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.Property;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.annotations.Transient;
import net.runeduniverse.libs.rogm.model.relations.Slot;

@Getter
@Setter
public class Inventory extends AEntity {
	@Relationship
	private Set<Slot> slots = new HashSet<>();

	@Property
	private Integer size = -1;
	@Transient
	private String inventory = null;
}
