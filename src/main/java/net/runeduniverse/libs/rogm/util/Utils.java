package net.runeduniverse.libs.rogm.util;

public class Utils {

	public static boolean isBlank(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim().isEmpty();
	}

}
