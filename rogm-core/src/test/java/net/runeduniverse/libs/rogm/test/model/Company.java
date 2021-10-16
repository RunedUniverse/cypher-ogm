package net.runeduniverse.libs.rogm.test.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Relationship;

@Getter
@Setter
@ToString(callSuper = true)
public class Company extends AEntity {

	private String name;

	@Relationship(label = "CREATED")
	private Set<Game> games = new HashSet<>();

	@Relationship(label = "OWNED_BY")
	private Company owner;
}
