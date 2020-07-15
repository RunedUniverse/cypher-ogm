package net.runeduniverse.libs.rogm.pattern;

import static net.runeduniverse.libs.rogm.util.Utils.isBlank;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;

import lombok.Data;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.lang.Language.DataFilter;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataNode;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataRelation;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Relation;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;

@Data
public class FieldPattern {

	private final PatternStorage storage;
	private final Field field;
	private final Class<?> type;
	private final String label;
	private final Direction direction;
	private final boolean collection;
	private final boolean defined;

	public FieldPattern(PatternStorage storage, Field field) throws Exception {
		this.storage = storage;
		this.field = field;
		this.field.setAccessible(true);
		Relationship fieldAnno = this.field.getAnnotation(Relationship.class);
		this.label = isBlank(fieldAnno.label()) ? this.field.getName() : fieldAnno.label();
		this.direction = fieldAnno.direction();
		Class<?> clazz = this.field.getType();
		this.collection = Collection.class.isAssignableFrom(clazz);
		if (this.collection)
			clazz = (Class<?>) ((ParameterizedType) this.field.getGenericType()).getActualTypeArguments()[0];
		this.type = clazz;

		if (this.type.isAnnotationPresent(NodeEntity.class))
			this.defined = false;
		else if (clazz.isAnnotationPresent(RelationshipEntity.class))
			this.defined = true;
		else
			throw new Exception("Unsupported Class<" + clazz.getName() + "> as @Relation found!");
	}

	public IFRelation queryRelation(IFNode origin) throws Exception {
		Relation relation = null;
		if (this.defined)
			relation = this.storage.getRelation(this.type).createFilter(origin, this.direction);
		else {
			relation = this.storage.getFactory().createRelation(this.direction);
			relation.setStart(origin);
			relation.setTarget(this._getNode(this.type, relation));
		}

		if (relation.getLabels().isEmpty())
			relation.getLabels().add(this.label);

		relation.setReturned(true);
		relation.setOptional(true);
		return relation;
	}

	private IFNode _getNode(Class<?> type, IFRelation relation) throws Exception {
		NodePattern node = this.storage.getNode(type);
		if (node == null)
			throw new Exception("Unsupported Class<" + type.getName() + "> as @Relation found!");
		return node.createFilter(relation);
	}

	public void saveRelation(Object entity, IDataNode node, Map<Object, DataFilter> includedData) throws Exception {
		if (this.collection)
			// Collection
			for (Object relNode : (Collection<?>) this.field.get(entity))
				node.getRelations().add(_getRelation(node, relNode, includedData));
		else {
			// Variable
			Object relNode = this.field.get(entity);
			if (relNode != null)
				node.getRelations().add(_getRelation(node, relNode, includedData));
		}
	}

	private IDataRelation _getRelation(IDataNode node, Object relEntity, Map<Object, DataFilter> includedData)
			throws Exception {

		IDataRelation relation = null;
		// clazz could be substituted with this.type but isn't in case the entities type
		// is a child of this.type
		Class<?> clazz = relEntity.getClass();
		if (clazz.isAnnotationPresent(RelationshipEntity.class))
			relation = this.storage.getRelation(clazz).createFilter(relEntity, node, this.direction, includedData);
		else {
			relation = this.storage.getFactory().createDataRelation(this.direction, null);
			relation.setFilterType(FilterType.UPDATE);
			relation.setStart(node);
			relation.setTarget(this.storage.getNode(clazz).createFilter(relEntity, includedData, false));
		}

		if (relation.getLabels().isEmpty())
			relation.getLabels().add(this.label);

		return relation;
	}

	public void setValue(Object holder, Object value) throws IllegalArgumentException {
		try {
			this.field.set(holder, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public Object getValue(Object holder) throws IllegalArgumentException {
		try {
			return this.field.get(holder);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
