package net.runeduniverse.libs.rogm.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Relationship;

@Getter
@Setter
@ToString
public class Game extends AEntity {

	private String name;

	@Relationship(label = "CHARACTER")
	private Set<Person> characters = new HashSet<Person>();

	@Relationship(label = "TRAILER")
	private Video trailer;
}
