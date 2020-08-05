package net.runeduniverse.libs.rogm.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.IConverter.UUIDConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.PostDelete;
import net.runeduniverse.libs.rogm.annotations.PostLoad;
import net.runeduniverse.libs.rogm.annotations.PostSave;
import net.runeduniverse.libs.rogm.annotations.PreDelete;
import net.runeduniverse.libs.rogm.annotations.PreSave;
import net.runeduniverse.libs.rogm.annotations.Relationship;

@Getter
@NodeEntity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Player {

	@Id(converter = UUIDConverter.class)
	@Setter
	private UUID uuid;

	@Setter
	private String name;
	
	@Setter
	@Relationship(label = "PLAYER_INV", direction = Direction.OUTGOING)
	private Inventory inventory;

	@PreSave
	private void preSave() {
		System.out.println("[PRE-SAVE] " + toString());
	}

	@PostSave
	private void postSave() {
		System.out.println("[POST-SAVE] " + toString());
	}

	@PostLoad
	private void postLoad() {
		System.out.println("[POST-LOAD] " + toString());
	}

	@PreDelete
	public void preDelete() {
		System.out.println("[PRE-DELETE] " + toString());
	}

	@PostDelete
	public void postDelete() {
		System.out.println("[POST-DELETE] " + toString());
	}
}
