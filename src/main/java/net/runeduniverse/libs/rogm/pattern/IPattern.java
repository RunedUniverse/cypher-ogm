package net.runeduniverse.libs.rogm.pattern;

import net.runeduniverse.libs.rogm.querying.IFilter;

public interface IPattern {
	public EntityType getEntityType();
	public IFilter createFilter(int depth);
}
