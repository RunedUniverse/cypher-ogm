package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class IDFilterNode<ID extends Serializable> extends FilterNode implements IIdentified<ID> {

	@Getter
	private ID id;
	
}
