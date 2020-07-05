package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class AIdFilter<ID extends Serializable> implements IIdentified<ID>{
	
	@Getter
	private ID id;

}
