package net.runeduniverse.libs.rogm.buffer;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.runeduniverse.libs.rogm.annotations.PostDelete;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.INodePattern;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pipeline.chain.Chain;
import net.runeduniverse.libs.rogm.pipeline.chain.ChainManager;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.Result;

public class BasicBuffer implements IBuffer {

	private Map<Object, Entry> entries = new HashMap<>();
	private Map<Class<?>, TypeEntry> typeMap = new HashMap<>();

	public BasicBuffer() {
		ChainManager.addChainLayers(BasicBuffer.class);
	}

	@SuppressWarnings("deprecation")
	@Chain(label = Chain.BUFFER_LOAD_CHAIN, layers = { 20 })
	public static Object acquireBuffered(final IBuffer buffer, IBaseQueryPattern pattern, IData data,
			LazyEntriesContainer lazyEntries, Result<?> result) throws Exception {
		LoadState loadState = data.getLoadState();
		TypeEntry te = buffer.getTypeEntry(pattern.getType());
		if (te != null) {
			Entry entry = te.idMap.get(data.getId());
			if (entry != null)
				return result.setResult(LoadState.merge(entry, loadState, lazyEntries));
		}
		return null;
	}

	@Chain(label = Chain.BUFFER_LOAD_CHAIN, layers = { 30 })
	public static EntityContainer parseData(final Parser.Instance parser, IBaseQueryPattern pattern, IData data)
			throws Exception {
		return new EntityContainer(parser.deserialize(pattern.getType(), data.getData()));
	}

	@Chain(label = Chain.BUFFER_LOAD_CHAIN, layers = { 40 })
	public static Object acquireNew(final IBuffer buffer, IBaseQueryPattern pattern, IData data,
			LazyEntriesContainer lazyEntries, EntityContainer container, Result<?> result) throws Exception {
		Object entity = container.getEnitity();
		LoadState loadState = data.getLoadState();
		pattern.setId(entity, data.getEntityId());
		Entry entry = new Entry(data, entity, loadState, pattern);
		if (lazyEntries != null && loadState == LoadState.LAZY)
			lazyEntries.addEntry(entry);
		buffer.addEntry(entry);
		return result.setResult(entity);
	}

	@Override
	public Entry update(Parser.Instance parser, Object entity, IPattern.IData data) throws Exception {
		if (entity == null)
			return null;
		Entry entry = entries.get(entity);

		parser.deserialize(entity, data.getData());
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
	public void addEntry(Serializable id, Serializable entityId, Object entity, LoadState loadState,
			IBaseQueryPattern pattern) {
		addEntry(new Entry(id, entityId, entity, loadState, entity.getClass(), pattern));
	}

	@Override
	public void updateEntry(Archive archive, Serializable id, Serializable entityId, Object entity, LoadState loadState)
			throws Exception {
		if (entity == null)
			return;
		Entry entry = entries.get(entity);

		Class<?> type = entity.getClass();
		IBaseQueryPattern pattern = archive.getPattern(type, IBaseQueryPattern.class);
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

	@Override
	public TypeEntry getTypeEntry(Class<?> type) {
		return this.typeMap.get(type);
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
