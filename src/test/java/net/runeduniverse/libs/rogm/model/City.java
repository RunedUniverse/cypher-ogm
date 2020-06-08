package net.runeduniverse.libs.rogm.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;

@NodeEntity
@Data
@ToString
@NoArgsConstructor
public class City {
	String name;
	List<House> houses = new ArrayList<>();
}
