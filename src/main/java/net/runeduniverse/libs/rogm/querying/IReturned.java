package net.runeduniverse.libs.rogm.querying;

// in case of <FilterType.DELETE> everything that gets returned will be deleted

public interface IReturned extends IFilter {
	public boolean isReturned();
}
