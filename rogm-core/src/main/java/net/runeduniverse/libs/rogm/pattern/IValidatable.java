package net.runeduniverse.libs.rogm.pattern;

import java.util.Collection;

public interface IValidatable {
	void validate() throws Exception;

	public static void validate(Object obj) throws Exception {
		if (obj instanceof IValidatable)
			((IValidatable) obj).validate();
	}

	public static void validate(Collection<Object> col) throws Exception {
		for (Object obj : col)
			IValidatable.validate(obj);
	}
}
