package net.runeduniverse.libs.rogm.test.model.relations;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.TargetNode;
import net.runeduniverse.libs.rogm.test.model.Actor;
import net.runeduniverse.libs.rogm.test.model.Person;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.annotations.StartNode;

@RelationshipEntity(direction = Direction.OUTGOING, label = "PLAYS")
@Getter
public class ActorPlaysPersonRelation extends ARelationEntity {

	@StartNode
	private Actor actor;
	@TargetNode
	private Person person;

}
