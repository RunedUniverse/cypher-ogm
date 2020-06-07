package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class IDFilter<ID extends Serializable> implements IdentifiedFilter<ID>{
	
	@Getter
	private ID id;

}
