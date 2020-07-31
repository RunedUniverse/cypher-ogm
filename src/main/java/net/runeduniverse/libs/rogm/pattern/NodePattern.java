package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.runeduniverse.libs.rogm.util.Utils.isBlank;

import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.IBuffer.Entry;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataNode;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Node;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class NodePattern extends APattern {

	private Set<String> labels = new HashSet<>();
	private Set<FieldPattern> relFields = new HashSet<>();

	public NodePattern(PatternStorage storage, Class<?> type) throws Exception {
		super(storage, type);
		this._parse(this.type);
	}

	private void _parse(Class<?> type) throws Exception {
		NodeEntity typeAnno = type.getAnnotation(NodeEntity.class);
		String label = typeAnno.label();
		if (isBlank(label) && !Modifier.isAbstract(type.getModifiers()))
			label = type.getSimpleName();
		if (!isBlank(label))
			this.labels.add(label);

		for (Field field : type.getDeclaredFields()) {
			field.setAccessible(true);
			if (this.parseId(field))
				continue;

			if (field.isAnnotationPresent(Relationship.class))
				this.relFields.add(new FieldPattern(this.storage, field));
		}
		this.parseMethods(type);

		if (type.getSuperclass().equals(Object.class))
			return;
		_parse(type.getSuperclass());
	}

	public IFilter search(boolean lazy) throws Exception {
		return this._search(this.storage.getFactory().createNode(this.labels, new ArrayList<>()), lazy, false);
	}

	public IFilter search(Serializable id, boolean lazy) throws Exception {
		return this._search(
				this.storage.getFactory().createIdNode(this.labels, new ArrayList<>(), id, this.idConverter), lazy,
				false);
	}

	public IFNode search(IFRelation caller, boolean lazy) {
		// includes ONLY the caller-relation filter
		Node node = this.storage.getFactory().createNode(this.labels, Arrays.asList(caller));
		try {
			return this._search(node, lazy, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}

	private IFNode _search(Node node, boolean lazy, boolean optional) throws Exception {
		node.setPattern(this);
		node.setReturned(true);
		if (optional)
			node.setOptional(true);
		if (lazy)
			node.setLazy(true);
		else
			for (FieldPattern field : this.relFields)
				node.getRelations().add(field.queryRelation(node));
		return node;
	}

	@Override
	public ISaveContainer save(Object entity, boolean lazy) throws Exception {
		Map<Object, IDataContainer> includedData = new HashMap<>();
		return new ISaveContainer() {

			@Override
			public IDataContainer getDataContainer() throws Exception {
				return createFilter(entity, includedData, !lazy);
			}

			@Override
			public void postSave() {
				for (Object object : includedData.keySet())
					try {
						storage.getPattern(object.getClass()).postSave(object);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}

			@Override
			public IFilter getRelatedFilter() throws Exception {
				Entry entry = storage.getBuffer().getEntry(entity);
				if (entry == null)
					return null;
				return search(entry.getId(), false);
			}
		};
	}

	@Override
	public IDeleteContainer delete(Object entity) throws Exception {
		IBuffer.Entry entry = this.storage.getBuffer().getEntry(entity);
		if (entry == null)
			throw new Exception("Node-Entity of type<" + entity.getClass().getName() + "> is not loaded!");

		preDelete(entity);

		Node node = this.storage.getFactory().createIdNode(null, null, entry.getId(), null);
		node.setReturned(true);
		return new DeleteContainer(this, entity, entry.getId(),
				this.storage.getFactory().createEffectedFilter(entry.getId()), node);
	}

	public IDataNode createFilter(Object entity, Map<Object, IDataContainer> includedData, boolean includeRelations)
			throws Exception {
		if (includedData.containsKey(entity))
			return (IDataNode) includedData.get(entity);

		this.preSave(entity);

		IDataNode node = null;
		if (this.isIdSet(entity)) {
			// update (id)
			node = this.storage.getFactory().createIdDataNode(this.labels, new ArrayList<>(), this.getId(entity),
					this.idConverter, entity);
			node.setFilterType(FilterType.UPDATE);
		} else {
			// create (!id)
			node = this.storage.getFactory().createDataNode(this.labels, new ArrayList<>(), entity);
			node.setFilterType(FilterType.CREATE);
		}
		node.setReturned(true);
		includedData.put(entity, node);

		if (includeRelations)
			for (FieldPattern field : this.relFields)
				field.saveRelation(entity, node, includedData);

		return node;
	}

	public void setRelation(Direction direction, String label, Object entity, Object value) {
		for (FieldPattern field : this.relFields)
			if (field.getDirection().equals(direction) && field.getLabel().equals(label)
					&& field.getType().isAssignableFrom(value.getClass())) {
				field.putValue(entity, value);
				return;
			}
	}

	@Override
	public void deleteRelations(Object entity, Collection<Object> delEntries) {
		for (FieldPattern field : this.relFields)
			field.removeValues(entity, delEntries);
	}
}
