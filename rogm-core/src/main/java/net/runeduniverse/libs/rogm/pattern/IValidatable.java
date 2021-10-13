package net.runeduniverse.libs.rogm.pattern;

import java.util.Collection;

public interface IValidatable {
	
	void validate() throws Exception;

	public static void validate(Object... values) throws Exception {
		if (values == null)
			return;

		for (Object obj : values) {
			if (obj instanceof IValidatable)
				((IValidatable) obj).validate();
			if (obj instanceof Collection<?>)
				for (Object element : (Collection<?>) obj)
					IValidatable.validate(element);
		}
	}
}
