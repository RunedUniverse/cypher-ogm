package net.runeduniverse.libs.rogm.querying;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class IDFilter implements IdentifiedFilter{
	
	@Getter
	private long id;

}
