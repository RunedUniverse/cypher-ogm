package net.runeduniverse.libs.rogm.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.IConverter.UUIDConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;

@Getter
@NodeEntity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Player {

	@Id(converter = UUIDConverter.class)
	private UUID uuid;

	@Setter
	private String name;
}
