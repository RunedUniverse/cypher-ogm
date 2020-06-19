package net.runeduniverse.libs.rogm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Address extends AEntity {
	String street;
	Integer number;
}
