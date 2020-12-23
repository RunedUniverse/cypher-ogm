package net.runeduniverse.libs.rogm.querying;

public interface IDataContainer extends IFilter {
	Object getData();

	boolean persist();

	boolean isReadonly();
}
