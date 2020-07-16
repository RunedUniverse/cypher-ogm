package net.runeduniverse.libs.rogm.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class City extends AEntity {
	String name;
	List<House> houses = new ArrayList<>();
}
