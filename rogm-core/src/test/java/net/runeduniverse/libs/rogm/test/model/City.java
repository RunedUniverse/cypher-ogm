package net.runeduniverse.libs.rogm.test.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@ToString(callSuper = true)
public class City extends AEntity {
	String name;
	List<House> houses = new ArrayList<>();
}
