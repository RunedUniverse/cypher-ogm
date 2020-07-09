package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.runeduniverse.libs.rogm.util.Utils.isBlank;

import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.lang.Language.DataFilter;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.DataNode;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.DataRelation;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Node;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Relation;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.util.Buffer;

public class NodePattern extends APattern {

	private Set<String> labels = new HashSet<>();
	private Set<Field> relFields = new HashSet<>();

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

			if (!field.isAnnotationPresent(Relationship.class))
				continue;

			Class<?> clazz = field.getType();
			if (Collection.class.isAssignableFrom(clazz))
				clazz = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

			if (!(clazz.isAnnotationPresent(NodeEntity.class) || clazz.isAnnotationPresent(RelationshipEntity.class)))
				throw new Exception("Unsupported Class<" + clazz.getName() + "> as @Relation found!");
			this.relFields.add(field);
		}
		if (type.getSuperclass().equals(Object.class))
			return;
		_parse(type.getSuperclass());
	}

	public IFilter createFilter() throws Exception {
		List<IFilter> relations = new ArrayList<>();
		Node node = this.storage.getFactory().createNode(this.labels, relations);
		node.setPattern(this);
		node.setReturned(true);
		_createFilterRelations(node, relations);

		return node;// includes ALL relation filters
	}

	public IFilter createIdFilter(Serializable id) throws Exception {
		List<IFilter> relations = new ArrayList<>();
		Node node = this.storage.getFactory().createIdNode(this.labels, relations, id);
		node.setPattern(this);
		node.setReturned(true);
		_createFilterRelations(node, relations);
		return node;
	}

	private void _createFilterRelations(IFNode node, List<IFilter> relations) throws Exception {
		for (Field field : this.relFields) {
			Relationship fieldAnno = field.getAnnotation(Relationship.class);
			Class<?> clazz = field.getType();
			if (Collection.class.isAssignableFrom(clazz))
				clazz = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

			Relation relation = null;
			if (clazz.isAnnotationPresent(RelationshipEntity.class))
				relation = this.storage.getRelation(clazz).createFilter(node, fieldAnno.direction());
			else {
				relation = this.storage.getFactory().createRelation(fieldAnno.direction());
				relation.setStart(node);
				relation.setTarget(this._getNode(clazz, relation));
			}

			if (relation.getLabels().isEmpty())
				relation.getLabels().add(isBlank(fieldAnno.label()) ? field.getName() : fieldAnno.label());

			relation.setReturned(true);
			relation.setOptional(true);
			relations.add(relation);
		}
	}

	public IFNode createFilter(IFRelation caller) {
		List<IFilter> relations = new ArrayList<>();
		relations.add(caller);
		Node node = this.storage.getFactory().createNode(this.labels, relations);
		node.setPattern(this);
		node.setReturned(true);
		node.setOptional(true);
		return node;// includes ONLY 1 relation filters
	}

	@Override
	public DataFilter createFilter(Object entity) throws Exception {
		return this.createFilter(entity, new HashMap<>());
	}

	public DataNode createFilter(Object entity, Map<Object, DataFilter> includedData) throws Exception {
		if (includedData.containsKey(entity))
			return (DataNode) includedData.get(entity);

		List<IFilter> relations = new ArrayList<>();
		DataNode node = null;
		if (this.isIdSet(entity)) {
			// update (id)
			node = this.storage.getFactory().createIdDataNode(this.labels, relations, this.getId(entity), entity);
			node.setFilterType(FilterType.UPDATE);
		} else {
			// create (!id)
			node = this.storage.getFactory().createDataNode(this.labels, relations, entity);
			node.setFilterType(FilterType.CREATE);
		}
		node.setReturned(true);
		includedData.put(entity, node);

		for (Field field : this.relFields) {
			Relationship fieldAnno = field.getAnnotation(Relationship.class);
			if (Collection.class.isAssignableFrom(field.getType()))
				// Collection
				for (Object relNode : (Collection<?>) field.get(entity))
					relations.add(_getRelation(node, fieldAnno, field.getName(), relNode, includedData));
			else {
				// Variable
				Object relNode = field.get(entity);
				if (relNode != null)
					relations.add(_getRelation(node, fieldAnno, field.getName(), relNode, includedData));
			}
		}

		return node;
	}

	private DataRelation _getRelation(DataNode node, Relationship anno, String fieldName, Object relEntity,
			Map<Object, DataFilter> includedData) throws Exception {

		// TODO retrieve DataRelations for all relations

		DataRelation relation = null;
		Class<?> clazz = relEntity.getClass();
		if (clazz.isAnnotationPresent(RelationshipEntity.class))
			relation = this.storage.getRelation(clazz).createFilter(relEntity, node, anno.direction(), includedData);
		else {
			relation = this.storage.getFactory().createDataRelation(anno.direction(), null);
			relation.setFilterType(FilterType.UPDATE);
			relation.setStart(node);
			relation.setTarget(this.storage.getNode(clazz).createFilter(relEntity, includedData));
		}

		if (relation.getLabels().isEmpty())
			relation.getLabels().add(isBlank(anno.label()) ? fieldName : anno.label());

		return relation;
	}

	private IFNode _getNode(Class<?> type, IFRelation relation) throws Exception {
		NodePattern node = this.storage.getNode(type);
		if (node == null)
			throw new Exception("Unsupported Class<" + type.getName() + "> as @Relation found!");
		return node.createFilter(relation);
	}

	@Override
	public Object parse(List<Data> data) throws Exception {
		Data primary = data.get(0);
		Object node = this.getBuffer().acquire(primary.getId(),
				this.type/* ((IPatternContainer) primary.getFilter()).getPattern().getType() */,
				this.parse(primary.getId(), primary.getData()));

		// TODO Auto-generated method stub
		return node;
	}

}
