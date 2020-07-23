package net.runeduniverse.libs.rogm.buffer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pattern.IStorage;

public class BasicBuffer implements IBuffer {

	protected IStorage storage = null;
	private List<Entry> entries = new ArrayList<>();
	private Map<Class<?>, TypeEntry> typeMap = new HashMap<>();

	@Override
	public IBuffer initialize(IStorage storage) {
		this.storage = storage;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T acquire(IPattern pattern, IData data, Class<T> type) throws Exception {
		TypeEntry te = this.typeMap.get(type);
		if (te != null) {
			Entry entry = te.idMap.get(data.getId());
			if (entry != null)
				return (T) entry.getEntity();
		}

		T entity = this.storage.getParser().deserialize(type, data.getData());
		pattern.setId(entity, data.getEntityId());
		this.addEntry(new Entry(data, entity));
		return entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getById(Serializable id, Class<T> type) {
		TypeEntry te = this.typeMap.get(type);
		if (te == null)
			return null;

		return (T) te.idMap.get(id).getEntity();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getByEntityId(Serializable entityId, Class<T> type) {
		TypeEntry te = this.typeMap.get(type);
		if (te == null)
			return null;

		return (T) te.entityIdMap.get(entityId).getEntity();
	}

	@Override
	public void addEntry(Entry entry) {
		TypeEntry te = this.typeMap.get(entry.getType());
		if (te == null) {
			te = new TypeEntry();
			this.typeMap.put(entry.getType(), te);
		}

		this.entries.add(entry);
		te.idMap.put(entry.getId(), entry);
		te.entityIdMap.put(entry.getEntityId(), entry);
	}

	@Override
	public void addEntry(Serializable id, Serializable entityId, Object entity) {
		this.addEntry(new Entry(id, entityId, entity, entity.getClass()));
	}

	@Override
	public void updateEntry(Serializable id, Serializable entityId, Object entity) throws Exception {
		Entry entry = null;
		for (Entry e : entries)
			if (e.getEntity() == entity) {
				entry = e;
				break;
			}

		Class<?> type = entity.getClass();
		System.out.println("TYPE: "+type);
		IPattern pattern = this.storage.getPattern(type);
		System.out.println("PATTERN: "+pattern);
		entityId = pattern.prepareEntityId(id, entityId);
		System.out.println("ID: "+id);
		System.out.println("ENTITY ID: "+entityId);

		if (entry == null)
			this.addEntry(new Entry(id, entityId, entity, type));
		else
			this.updateEntry(entry, id, entityId);
		System.out.println("Object: "+entity);
		pattern.setId(entity, entityId);
	}

	protected void updateEntry(Entry entry, Serializable id, Serializable entityId) {
		TypeEntry te = this.typeMap.get(entry.getType());

		if (id != entry.getId()) {
			te.idMap.remove(entry.getId());
			entry.setId(id);
			te.idMap.put(id, entry);
		}
		if (entityId != entry.getEntityId()) {
			te.entityIdMap.remove(entry.getEntityId());
			entry.setEntityId(entityId);
			te.entityIdMap.put(entityId, entry);
		}
	}

	@Override
	public void removeEntry(Entry entry) {
		TypeEntry te = this.typeMap.get(entry.getType());
		if (te == null)
			return;
		te.idMap.remove(entry.getId());
		te.entityIdMap.remove(entry.getEntityId());
		this.entries.remove(entry);
	}

	@Override
	public void removeEntry(Object entity) {
		for (Entry entry : entries)
			if (entry.getEntity() == entity) {
				this.removeEntry(entry);
				return;
			}
	}

	@Override
	public List<Entry> getAllEntries() {
		return this.entries;
	}

	protected class TypeEntry {
		protected Map<Serializable, Entry> idMap = new HashMap<>();
		protected Map<Serializable, Entry> entityIdMap = new HashMap<>();
	}
}
