package net.runeduniverse.libs.rogm.buffer;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pattern.IStorage;;

public interface IBuffer {

	IBuffer initialize(IStorage storage);

	<T> T acquire(IData data, Class<T> type) throws Exception;

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

	void addEntry(Entry entry);

	void addEntry(Serializable id, Serializable entityId, Object entity);

	void removeEntry(Entry entry);

	void removeEntry(Object entity);

	List<Entry> getAllEntries();

	@Data
	@AllArgsConstructor
	public class Entry {
		private Serializable id;
		private Serializable entityId;
		private Object entity;
		private Class<?> type;

		public Entry(IData data, Object entity) {
			this.id = data.getId();
			this.entityId = null;
			this.entity = entity;
			this.type = entity.getClass();
		}
	}
}
