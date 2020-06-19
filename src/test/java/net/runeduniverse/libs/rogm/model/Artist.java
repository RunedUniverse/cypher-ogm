package net.runeduniverse.libs.rogm.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Relationship;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Artist extends Person {

	@Relationship(label = "SINGS")
	private Set<Song> sings = new HashSet<>();

	@Relationship(label = "CREATED")
	private Set<Song> created = new HashSet<>();

}
