package net.runeduniverse.libs.rogm.test.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.GeneratedValue;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;

@NodeEntity
@ToString
@Getter
@Setter
public abstract class AEntity {

	@Id
	@GeneratedValue
	// TODO: add compatibility to long = Long
	private Long myid;
}
