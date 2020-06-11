package net.runeduniverse.libs.rogm.model;

import java.util.UUID;

import lombok.Data;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Property;

@NodeEntity
@Data
@ToString
public class ShadowPlayer {

	@Id
	private Long id;
	@Property
	private UUID uuid;
	
	@Property
	private String name;

	@Property
	private Boolean hasPlayedBefore;

	@Property
	private Double maxHealth;
	@Property
	private Double health;
	@Property
	private Double absorption;

	@Property
	private Integer maxAir;
	@Property
	private Integer remAir;
	@Property
	private Float xpp;
	@Property
	private Integer xpl;
	@Property
	private Integer food;
	@Property
	private Float saturation;
	
}
