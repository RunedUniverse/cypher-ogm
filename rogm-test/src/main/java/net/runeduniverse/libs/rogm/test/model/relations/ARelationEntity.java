package net.runeduniverse.libs.rogm.test.model.relations;

import lombok.Getter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.GeneratedValue;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;

@RelationshipEntity
@ToString
@Getter
public abstract class ARelationEntity {

	@Id
	@GeneratedValue
	// TODO: add compatibility to long = Long
	private Long myid;
}
