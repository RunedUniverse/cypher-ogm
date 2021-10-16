package net.runeduniverse.libs.rogm.test.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.test.model.relations.ActorPlaysPersonRelation;

@Getter
@Setter
@ToString(callSuper = true)
public class Actor extends Person {

	@Relationship
	private Set<ActorPlaysPersonRelation> plays = new HashSet<>();

}
