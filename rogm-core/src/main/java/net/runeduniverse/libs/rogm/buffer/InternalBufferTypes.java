package net.runeduniverse.libs.rogm.buffer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.ILazyLoading;

public interface InternalBufferTypes {
	public class TypeEntry {
		protected TypeEntry() {
		}

		protected Map<Serializable, Entry> idMap = new HashMap<>();
		protected Map<Serializable, Entry> entityIdMap = new HashMap<>();

		public Entry getIdEntry(Serializable id) {
			return this.idMap.get(id);
		}

		public Entry getEntityIdEntry(Serializable id) {
			return this.entityIdMap.get(id);
		}
	}

	@Data
	@AllArgsConstructor
	public class Entry {
		private Serializable id;
		private Serializable entityId;
		private Object entity;
		private LoadState loadState;
		private Class<?> type;
		private IBaseQueryPattern pattern;

		public Entry(IData data, Object entity, LoadState loadState, IBaseQueryPattern pattern) {
			this.id = data.getId();
			this.entityId = data.getEntityId();
			this.entity = entity;
			this.loadState = loadState;
			this.type = entity.getClass();
			this.pattern = pattern;
		}
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

		public static Object merge(Entry entry, LoadState state, LazyEntriesContainer lazyEntries) {
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
