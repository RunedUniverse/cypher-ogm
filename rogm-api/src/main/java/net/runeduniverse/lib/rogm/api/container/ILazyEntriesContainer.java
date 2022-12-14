package net.runeduniverse.lib.rogm.api.container;

import java.util.Collection;
import java.util.Set;

import net.runeduniverse.lib.rogm.api.buffer.IEntry;

public interface ILazyEntriesContainer {
	public Set<IEntry> getLazyEntries();

	public boolean addEntry(IEntry entry);

	public boolean addAllEntries(Collection<IEntry> entries);

	public boolean addEntries(ILazyEntriesContainer container);

	public boolean isEmpty();

	public void clear();
}
