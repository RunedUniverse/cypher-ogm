package net.runeduniverse.lib.rogm.api.container;

import java.util.Map;
import java.util.Set;

import net.runeduniverse.lib.rogm.api.buffer.IBuffer;
import net.runeduniverse.lib.rogm.api.pattern.IArchive;
import net.runeduniverse.lib.rogm.api.querying.IDataContainer;
import net.runeduniverse.lib.rogm.api.querying.IFilter;
import net.runeduniverse.lib.rogm.api.querying.IQueryBuilderInstance;

public interface ISaveContainer {
	public Map<Object, IQueryBuilderInstance<?, ?, ? extends IFilter>> getIncludedData();

	public IDataContainer getDataContainer();

	public Set<IFilter> calculateEffectedFilter(final IArchive archive, final IBuffer buffer) throws Exception;
}
