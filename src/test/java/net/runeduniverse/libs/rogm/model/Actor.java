package net.runeduniverse.libs.rogm.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.model.relations.ActorPlaysPersonRelation;

@Getter
@Setter
@ToString
public class Actor extends Person {
	
	@Relationship
	private Set<ActorPlaysPersonRelation> plays = new HashSet<>();

}
