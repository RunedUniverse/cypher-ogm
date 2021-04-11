package net.runeduniverse.libs.rogm.buffer;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.runeduniverse.libs.rogm.annotations.PostDelete;
import net.runeduniverse.libs.rogm.pattern.INodePattern;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IStorage;

public class BasicBuffer implements IBuffer {

	protected IStorage storage = null;
	private Map<Object, Entry> entries = new HashMap<>();
	private Map<Class<?>, TypeEntry> typeMap = new HashMap<>();

	@Override
	public IBuffer initialize(IStorage storage) {
		this.storage = storage;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T acquire(IPattern pattern, IPattern.IData data, Class<T> type, LoadState loadState,
			Set<Entry> lazyEntries) throws Exception {
		TypeEntry te = this.typeMap.get(type);
		if (te != null) {
			Entry entry = te.idMap.get(data.getId());
			if (entry != null)
				return (T) LoadState.merge(entry, loadState, lazyEntries);
		}

		T entity = this.storage.getParser()
				.deserialize(type, data.getData());
		pattern.setId(entity, data.getEntityId());
		Entry entry = new Entry(data, entity, loadState, pattern);
		if (lazyEntries != null && loadState == LoadState.LAZY)
			lazyEntries.add(entry);
		addEntry(entry);
		return entity;
	}

	@Override
	public Entry update(Object entity, IPattern.IData data) throws Exception {
		if (entity == null)
			return null;
		Entry entry = entries.get(entity);

		this.storage.getParser()
				.deserialize(entity, data.getData());
		updateEntry(entry, data.getId(), data.getEntityId());
		entry.getPattern()
				.setId(entity, data.getEntityId());
		return entry;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getById(Serializable id, Class<T> type) {
		TypeEntry te = this.typeMap.get(type);
		if (te == null || te.idMap.get(id) == null)
			return null;

		return (T) te.idMap.get(id)
				.getEntity();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getByEntityId(Serializable entityId, Class<T> type) {
		TypeEntry te = this.typeMap.get(type);
		if (te == null || te.entityIdMap.get(entityId) == null)
			return null;

		return (T) te.entityIdMap.get(entityId)
				.getEntity();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCompleteByEntityId(Serializable entityId, Class<T> type) {
		TypeEntry te = this.typeMap.get(type);
		if (te == null)
			return null;
		Entry entry = te.entityIdMap.get(entityId);
		if (entry == null || entry.getLoadState() == LoadState.LAZY)
			return null;

		return (T) entry.getEntity();
	}

	@Override
	public void addEntry(Entry entry) {
		TypeEntry te = this.typeMap.get(entry.getType());
		if (te == null) {
			te = new TypeEntry();
			this.typeMap.put(entry.getType(), te);
		}

		this.entries.put(entry.getEntity(), entry);
		te.idMap.put(entry.getId(), entry);
		te.entityIdMap.put(entry.getEntityId(), entry);
	}

	@Override
	public void addEntry(Serializable id, Serializable entityId, Object entity, LoadState loadState, IPattern pattern) {
		addEntry(new Entry(id, entityId, entity, loadState, entity.getClass(), pattern));
	}

	@Override
	public void updateEntry(Serializable id, Serializable entityId, Object entity, LoadState loadState)
			throws Exception {
		if (entity == null)
			return;
		Entry entry = entries.get(entity);

		Class<?> type = entity.getClass();
		IPattern pattern = this.storage.getPattern(type);
		entityId = pattern.prepareEntityId(id, entityId);

		if (entry == null)
			addEntry(new Entry(id, entityId, entity, loadState, type, pattern));
		else
			updateEntry(entry, id, entityId);
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
		if (entry == null)
			return;
		TypeEntry te = this.typeMap.get(entry.getType());
		if (te == null)
			return;
		te.idMap.remove(entry.getId());
		te.entityIdMap.remove(entry.getEntityId());
		this.entries.remove(entry.getEntity());
	}

	@Override
	public void removeEntry(Object entity) {
		removeEntry(this.entries.get(entity));
	}

	@Override
	public Collection<Entry> getAllEntries() {
		return this.entries.values();
	}

	protected class TypeEntry {
		protected Map<Serializable, Entry> idMap = new HashMap<>();
		protected Map<Serializable, Entry> entityIdMap = new HashMap<>();
	}

	@Override
	public Entry getEntry(Object entity) {
		return this.entries.get(entity);
	}

	@Override
	public void eraseRelations(Serializable deletedId, Serializable relationId, Serializable nodeId) {
		Set<Entry> deletedEntries = new HashSet<>();
		Set<Object> deletedEntities = new HashSet<>();
		Set<Entry> nodes = new HashSet<>();

		for (Entry entry : this.entries.values())
			if (entry.getId()
					.equals(deletedId)
					|| entry.getId()
							.equals(relationId)) {
				deletedEntries.add(entry);
				deletedEntities.add(entry.getEntity());
			} else if (entry.getId()
					.equals(nodeId))
				nodes.add(entry);

		for (Entry entry : nodes)
			((INodePattern) entry.getPattern()).deleteRelations(entry.getEntity(), deletedEntities);

		for (Entry entry : deletedEntries) {
			removeEntry(entry);
			entry.getPattern()
					.callMethod(PostDelete.class, entry.getEntity());
		}
	}
}
