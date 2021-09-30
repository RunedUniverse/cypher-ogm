package net.runeduniverse.libs.rogm.test.model.relations;

import lombok.Getter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.GeneratedValue;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.test.system.TestModelRelation;

@RelationshipEntity
@ToString
@Getter
public abstract class ARelationEntity implements TestModelRelation {

	@Id
	@GeneratedValue
	// TODO: add compatibility to long = Long
	private Long myid;
}
