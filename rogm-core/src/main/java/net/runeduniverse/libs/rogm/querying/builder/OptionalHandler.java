package net.runeduniverse.libs.rogm.querying.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.querying.IOptional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OptionalHandler implements IOptional, NoFilterType {

	private boolean optional = false;
}
