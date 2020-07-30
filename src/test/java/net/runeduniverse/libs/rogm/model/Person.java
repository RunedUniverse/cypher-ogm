package net.runeduniverse.libs.rogm.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.PostDelete;
import net.runeduniverse.libs.rogm.annotations.PostLoad;
import net.runeduniverse.libs.rogm.annotations.Post⁮Save;
import net.runeduniverse.libs.rogm.annotations.PreDelete;
import net.runeduniverse.libs.rogm.annotations.PreSave;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Person extends AEntity {

	private String firstName;
	private String lastName;
	private boolean fictional;

	public Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Person(String firstName, String lastName, boolean fictional) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.fictional = fictional;
	}

	@PreSave
	private void preSave() {
		System.out.println("[PRE-SAVE] " + toString());
	}

	@Post⁮Save
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
