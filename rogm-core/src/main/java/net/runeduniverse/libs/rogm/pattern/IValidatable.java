package net.runeduniverse.libs.rogm.pattern;

public interface IValidatable {
	void validate() throws Exception;

	public static void validate(Object obj) throws Exception {
		if (obj instanceof IValidatable)
			((IValidatable) obj).validate();
	}
}
