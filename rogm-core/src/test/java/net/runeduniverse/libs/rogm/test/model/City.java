package net.runeduniverse.libs.rogm.test.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class City extends AEntity {
	String name;
	List<House> houses = new ArrayList<>();
}
