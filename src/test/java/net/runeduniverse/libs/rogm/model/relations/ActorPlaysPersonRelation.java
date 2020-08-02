package net.runeduniverse.libs.rogm.model.relations;

import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.TargetNode;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.annotations.StartNode;
import net.runeduniverse.libs.rogm.model.Actor;
import net.runeduniverse.libs.rogm.model.Person;

@RelationshipEntity(direction = Direction.OUTGOING, label = "PLAYS")
public class ActorPlaysPersonRelation {

	@StartNode
	private Actor actor;
	@TargetNode
	private Person person;

}
