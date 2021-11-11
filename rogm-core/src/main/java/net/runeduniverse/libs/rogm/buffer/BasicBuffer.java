/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.libs.rogm.buffer;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.runeduniverse.libs.chain.ChainManager;
import net.runeduniverse.libs.rogm.annotations.PostDelete;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.INodePattern;
import net.runeduniverse.libs.rogm.pipeline.chain.data.UpdatedEntryContainer;

public class BasicBuffer implements IBuffer, InternalBufferTypes {

	private Map<Object, Entry> entries = new HashMap<>();
	private Map<Class<?>, TypeEntry> typeMap = new HashMap<>();

	@Override
	public void setupChainManager(ChainManager chainManager) throws Exception {
		chainManager.addChainLayers(BasicBufferLayers.class);
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
	public void addEntry(IEntry iEntry) {
		Entry entry = Entry.from(iEntry);
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
			IBaseQueryPattern<?> pattern) {
		addEntry(new Entry(id, entityId, entity, loadState, entity.getClass(), pattern));
	}

	@Override
	public void updateEntry(Archive archive, UpdatedEntryContainer container) {
		Object entity = container.getEntity();
		if (entity == null)
			return;
		Entry entry = entries.get(entity);

		Class<?> type = entity.getClass();
		IBaseQueryPattern<?> pattern = archive.getPattern(type, IBaseQueryPattern.class);
		pattern.prepareEntityId(container);

		if (entry == null)
			addEntry(new Entry(container.getId(), container.getEntityId(), entity, container.getLoadState(), type,
					pattern));
		else
			updateEntry(entry, container.getId(), container.getEntityId());
		pattern.setId(entity, container.getEntityId());
	}

	// INTERNAL USE
	@Deprecated
	public void updateEntry(Entry entry, Serializable id, Serializable entityId) {
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
	public void removeEntry(IEntry iEntry) {
		Entry entry = Entry.from(iEntry);
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
	public Collection<IEntry> getAllEntries() {
		Collection<IEntry> values = new HashSet<>();
		for (Entry entry : this.entries.values())
			values.add(entry);
		return values;
	}

	// INTERNAL USE
	@Deprecated
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
			((INodePattern<?>) entry.getPattern()).deleteRelations(entry.getEntity(), deletedEntities);

		for (Entry entry : deletedEntries) {
			this.removeEntry(entry);
			entry.getPattern()
					.callMethod(PostDelete.class, entry.getEntity());
		}
	}
}
