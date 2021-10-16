package net.runeduniverse.libs.rogm.test.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.GeneratedValue;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.test.system.TestModelNode;

@NodeEntity
@ToString
@Getter
@Setter
public abstract class AEntity implements TestModelNode {

	@Id
	@GeneratedValue
	// TODO: add compatibility to long = Long
	private Long myid;
}
