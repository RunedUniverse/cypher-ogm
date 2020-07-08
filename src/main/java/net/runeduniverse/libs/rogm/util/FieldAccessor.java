package net.runeduniverse.libs.rogm.util;

import java.io.Serializable;

public interface FieldAccessor {
	public <T extends Object, ID extends Serializable> T setObjectId(T obj, ID id);
}
