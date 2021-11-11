package net.runeduniverse.libs.rogm.buffer;

import java.io.Serializable;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.ILazyLoading;

public interface BufferTypes {

	public interface IEntry {
		Serializable getId();

		Serializable getEntityId();

		Object getEntity();

		LoadState getLoadState();

		Class<?> getType();

		IBaseQueryPattern<?> getPattern();

		void setId(Serializable id);

		void setEntityId(Serializable entityId);

		void setEntity(Object entity);

		void setLoadState(LoadState state);

		void setType(Class<?> type);

		void setPattern(IBaseQueryPattern<?> pattern);
	}

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
}
