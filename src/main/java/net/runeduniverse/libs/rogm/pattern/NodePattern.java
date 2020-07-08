package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.runeduniverse.libs.rogm.util.Utils.isBlank;

import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Node;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Relation;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class NodePattern implements IPattern {

	private final PatternStorage storage;
	private final Class<?> type;
	private Set<String> labels = new HashSet<>();
	private Field idField;
	private Set<Field> relFields = new HashSet<>();

	public NodePattern(PatternStorage storage, Class<?> type) {
		this.storage = storage;
		this.type = type;
		this._parse(this.type);
	}

	private void _parse(Class<?> type) {
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
				this.relFields.add(field);
		}
		if (type.getSuperclass().equals(Object.class))
			return;
		_parse(type.getSuperclass());
	}

	public IFilter createFilter() throws Exception {
		List<IFilter> relations = new ArrayList<>();
		Node node = this.storage.getFactory().createNode(this.labels, relations);
		node.setReturned(true);
		_createFilterRelations(node, relations);
		
		return node;// includes ALL relation filters
	}
	
	public IFilter createFilter(Object entity) throws Exception {
		List<IFilter> relations = new ArrayList<>();
		Node node = this.storage.getFactory().createIdNode(this.labels, relations, (Serializable) this.idField.get(entity));
		node.setReturned(true);
		_createFilterRelations(node, relations);
		return node;
	}
	
	private void _createFilterRelations(IFNode node, List<IFilter> relations) throws Exception {
		for (Field field : this.relFields) {
			Relationship fieldAnno = field.getAnnotation(Relationship.class);
			Class<?> clazz = field.getType();
			if(Collection.class.isAssignableFrom(clazz))
				clazz = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			
			Relation relation = null;
			if(clazz.isAnnotationPresent(RelationshipEntity.class))
				relation = this.storage.getRelation(clazz).createFilter(node, fieldAnno.direction());
			else if(clazz.isAnnotationPresent(NodeEntity.class))
			{
				relation = this.storage.getFactory().createRelation(fieldAnno.direction());
				relation.setStart(node);
				relation.setTarget(this._getNode(clazz, relation));
			}
			else
				throw new Exception("Unsupported Class<"+clazz.getName()+"> as @Relation found!");
			
			if(relation.getLabels().isEmpty())
				relation.getLabels().add(isBlank(fieldAnno.label())?field.getName():fieldAnno.label());
			
			relation.setReturned(true);
			relation.setOptional(true);
			relations.add(relation);
		}
	}

	public IFNode createFilter(IFRelation caller) {
		List<IFilter> relations = new ArrayList<>();
		relations.add(caller);
		Node node = this.storage.getFactory().createNode(this.labels, relations);
		node.setReturned(true);
		node.setOptional(true);
		return node;// includes ONLY 1 relation filters
	}
	
	private IFNode _getNode(Class<?> type, IFRelation relation) throws Exception {
		NodePattern node = this.storage.getNode(type);
		if (node == null)
			throw new Exception("Unsupported Class<"+type.getName()+"> as @Relation found!");
		return node.createFilter(relation);
	}

	@Override
	public Object setId(Object object, Serializable id) throws IllegalArgumentException {
		if (this.idField == null)
			return object;
		try {
			this.idField.set(object, id);
		} catch (IllegalAccessException e) {
		}
		return object;
	}

	@Override
	public Object parse(Serializable id, String data) throws Exception {
		return this.setId(this.storage.getParser().deserialize(this.type, data), id);
	}
}
