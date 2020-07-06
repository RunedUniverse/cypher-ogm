package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;

import net.runeduniverse.libs.rogm.querying.IFilter;

public interface IPattern {
	public EntityType getEntityType();
	public IFilter createFilter(int depth);
	public <ID extends Serializable> IFilter createFilter(int depth, ID id);
}
