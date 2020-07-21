package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;

public interface IIdentified<ID extends Serializable> extends IFilter {

	ID getId();

	public default Class<?> getIdType() {
		return getId().getClass();
	}

	public static Class<?> getIdType(IFilter filter) {
		if (!identify(filter))
			return null;
		return ((IIdentified<?>) filter).getIdType();
	}

	public static boolean identify(IFilter filter) {
		if (filter == null || !(filter instanceof IIdentified) || ((IIdentified<?>) filter).getId() == null)
			return false;
		return true;
	}
}
