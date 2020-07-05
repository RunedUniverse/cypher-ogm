package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class IDFilterRelation<ID extends Serializable> extends FilterRelation implements IIdentified<ID> {

	@Getter
	private ID id;
	
}
