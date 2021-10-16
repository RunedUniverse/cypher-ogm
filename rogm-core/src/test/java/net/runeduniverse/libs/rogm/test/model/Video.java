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
public class Video extends AEntity {

	@Relationship(label = "CONTAINS")
	private Set<Person> characters = new HashSet<>();

	@Relationship(label = "CONTAINS")
	private Set<Song> songs = new HashSet<>();
}
