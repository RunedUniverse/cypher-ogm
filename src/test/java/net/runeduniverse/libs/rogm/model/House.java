package net.runeduniverse.libs.rogm.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Transient;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class House extends AEntity {
	Address address;
	List<Person> people = new ArrayList<>();
	@Transient
	Boolean empty = true;
}
