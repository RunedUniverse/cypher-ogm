package net.runeduniverse.libs.rogm.buffer;

import java.io.Serializable;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.runeduniverse.libs.rogm.buffer.BasicBuffer.TypeEntry;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.ILazyLoading;;

public interface IBuffer {

	Entry update(Parser.Instance parser, Object entity, IData data) throws Exception;

	/***
	 * Load Entity defined by Id. The Id gets defined from the Database.
	 * 
	 * @param <Serializable> id
	 * @param <Class>        type of <T>
	 * @return the <Object> of the requested class <T>
	 */
	<T> T getById(Serializable id, Class<T> type);

	/***
	 * Load Entity defined by entityId, in some cases it matches the entityId. The
	 * Id can be defined in the Object.
	 * 
	 * @param <Serializable> id
	 * @param <Class>        type of <T>
	 * @return the <Object> of the requested class <T>
	 */
	<T> T getByEntityId(Serializable entityId, Class<T> type);

	<T> T getCompleteByEntityId(Serializable entityId, Class<T> type);

	void addEntry(Entry entry);

	void addEntry(Serializable id, Serializable entityId, Object entity, LoadState loadState, IBaseQueryPattern pattern);

	void updateEntry(Archive archive, Serializable id, Serializable entityId, Object entity, LoadState loadState)
			throws Exception;

	void removeEntry(Entry entry);

	void removeEntry(Object entity);

	void eraseRelations(Serializable deletedId, Serializable relationId, Serializable nodeId);

	Entry getEntry(Object entity);

	Collection<Entry> getAllEntries();

	@Deprecated
	TypeEntry getTypeEntry(Class<?> type);

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

		protected static Object merge(Entry entry, LoadState state, LazyEntriesContainer lazyEntries) {
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
