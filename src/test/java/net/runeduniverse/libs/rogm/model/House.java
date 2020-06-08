package net.runeduniverse.libs.rogm.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Transient;

@NodeEntity
@Data
@ToString
@NoArgsConstructor
public class House {
	Address address;
	List<Person> people;
	@Transient
	Boolean empty = true;
}
