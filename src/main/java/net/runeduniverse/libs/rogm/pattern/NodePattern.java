package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static net.runeduniverse.libs.rogm.util.Utils.isBlank;

import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.lang.Language.DataFilter;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataNode;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Node;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.util.Buffer;

public class NodePattern extends APattern {

	private Set<String> labels = new HashSet<>();
	private Set<FieldPattern> relFields = new HashSet<>();

	public NodePattern(PatternStorage storage, Class<?> type) throws Exception {
		super(storage, type);
		this._parse(this.type);
	}

	@Override
	public Buffer getBuffer() {
		return this.storage.getNodeBuffer();
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
			if (field.isAnnotationPresent(Id.class) && this.idField == null) {
				this.idField = field;
				continue;
			}

			if (field.isAnnotationPresent(Relationship.class))
				this.relFields.add(new FieldPattern(this.storage, field));
		}
		if (type.getSuperclass().equals(Object.class))
			return;
		_parse(type.getSuperclass());
	}

	public IFilter createFilter() throws Exception {
		Node node = this.storage.getFactory().createNode(this.labels, new ArrayList<>());
		node.setPattern(this);
		node.setReturned(true);
		for (FieldPattern field : this.relFields)
			node.getRelations().add(field.queryRelation(node));

		return node;// includes ALL relation filters
	}

	public IFilter createIdFilter(Serializable id) throws Exception {
		Node node = this.storage.getFactory().createIdNode(this.labels, new ArrayList<>(), id);
		node.setPattern(this);
		node.setReturned(true);
		for (FieldPattern field : this.relFields)
			node.getRelations().add(field.queryRelation(node));
		return node;
	}

	public IFNode createFilter(IFRelation caller) {
		Node node = this.storage.getFactory().createNode(this.labels, Arrays.asList(caller));
		node.setPattern(this);
		node.setReturned(true);
		node.setOptional(true);
		return node;// includes ONLY 1 relation filters
	}

	@Override
	public DataFilter createFilter(Object entity) throws Exception {
		return this.createFilter(entity, new HashMap<>(), true);
	}

	public IDataNode createFilter(Object entity, Map<Object, DataFilter> includedData, boolean includeRelations)
			throws Exception {
		if (includedData.containsKey(entity))
			return (IDataNode) includedData.get(entity);

		IDataNode node = null;
		if (this.isIdSet(entity)) {
			// update (id)
			node = this.storage.getFactory().createIdDataNode(this.labels, new ArrayList<>(), this.getId(entity),
					entity);
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
	
	public void parseRelation(Object entity, Data data) {
		
	}
}
