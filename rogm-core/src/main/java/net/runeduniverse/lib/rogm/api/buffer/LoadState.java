package net.runeduniverse.lib.rogm.api.buffer;

import net.runeduniverse.lib.rogm.api.querying.IFilter;
import net.runeduniverse.lib.rogm.api.querying.ILazyLoading;
import net.runeduniverse.lib.rogm.pipeline.chain.data.LazyEntriesContainer;

public enum LoadState {
	COMPLETE, LAZY;

	public static LoadState get(boolean lazy) {
		if (lazy)
			return LAZY;
		return COMPLETE;
	}

	public static LoadState get(IFilter filter) {
		return get(filter instanceof ILazyLoading && ((ILazyLoading) filter).isLazy());
	}

	public static Object merge(IEntry entry, LoadState state, LazyEntriesContainer lazyEntries) {
		if (entry.getLoadState() == COMPLETE || state == COMPLETE)
			entry.setLoadState(COMPLETE);
		else {
			entry.setLoadState(LAZY);
			if (lazyEntries != null)
				lazyEntries.addEntry(entry);
		}
		return entry.getEntity();
	}
}