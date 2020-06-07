package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;

public interface IdentifiedFilter<ID extends Serializable> extends Filter{

	 ID getId();
	 
	 public default <T extends Serializable> boolean checkType(Class<T> type) {
		 return type.isInstance(this.getId());
	 }
}
