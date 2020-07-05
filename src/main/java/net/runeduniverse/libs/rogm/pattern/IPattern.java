package net.runeduniverse.libs.rogm.pattern;

import net.runeduniverse.libs.rogm.querying.Filter;

public interface IPattern {
	public Filter createFilter(int depth);
}
