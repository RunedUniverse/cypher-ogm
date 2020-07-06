package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.IDFilterNode;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class NodePattern implements IPattern {

	public static final EntityType ENITIY_TYPE = EntityType.NODE;
	private final PatternStorage storage;
	private final Class<?> type;
	private Field idField;

	public NodePattern(PatternStorage storage, Class<?> type) {
		this.storage = storage;
		this.type = type;
		// TODO Parse all data from type
	}

	@Override
	public EntityType getEntityType() {
		return ENITIY_TYPE;
	}

	@Override
	public IFilter createFilter(int depth) {
		if(depth<1)
			return null;
		// TODO call recursively the other Patterns and acquire their filters (depth-1)
		FilterNode node = new FilterNode();
		// TODO add Labels & Relations
		return node;
	}

	@Override
	public <ID extends Serializable> IFilter createFilter(int depth, ID id) {
		if(depth<1)
			return null;
		// TODO call recursively the other Patterns and acquire their filters (depth-1)
		
		// TODO add Labels & Relations
		// class java.lang.Long
		if (Number.class.isAssignableFrom(idField.getType())) {
			// IIdentified
			return new IDFilterNode<ID>(id);
		}
		// ParamFilter
		return new FilterNode().addParam("_id", id);
	}

	// helper methods for later use
	public <T extends Object, ID extends Serializable> T setObjectId(T obj, ID id) {
		// no @Id field -> skip
		Field field = findAnnotatedField(obj.getClass(), Id.class);
		if (field != null)
			try {
				field.set(obj, field.getType().cast(id));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

		return obj;
	}

	public <ANNO extends Annotation> Field findAnnotatedField(Class<?> clazz, Class<ANNO> anno) {
		if (clazz.isAssignableFrom(Object.class))
			return null;
		for (Field field : clazz.getDeclaredFields())
			if (field.isAnnotationPresent(anno)) {
				field.setAccessible(true);
				return field;
			}
		return findAnnotatedField(clazz.getSuperclass(), anno);
	}
}
