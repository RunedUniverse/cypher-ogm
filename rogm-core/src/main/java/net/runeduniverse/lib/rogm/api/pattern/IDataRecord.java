package net.runeduniverse.lib.rogm.api.pattern;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import net.runeduniverse.lib.rogm.api.querying.IFilter;

public interface IDataRecord {
	IFilter getPrimaryFilter();

	Set<Serializable> getIds();

	List<Set<IData>> getData();
}