package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.EndNode;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.StartNode;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.IDFilterNode;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class NodePattern implements IPattern {

	private final PatternStorage storage;
	private final Class<?> type;
	private Field idField;

	public NodePattern(PatternStorage storage, Class<?> type) {
		this.storage = storage;
		this.type = type;
		this._parse(this.type);
	}
	
	private void _parse(Class<?> type) {
		for (Field field : type.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Id.class) && this.idField == null) {
				this.idField = field;
				continue;
			}

			// TODO Parse all data from type
		}
		if (type.getSuperclass().equals(Object.class))
			return;
		_parse(type.getSuperclass());
	}

	public IFilter createFilter() {
		return null;// includes ALL relation filters
	}
	public IFNode createFilter(IFRelation caller) {
		return null;// includes ONLY 1 relation filters
	}
	
	// TODO depth gets moved to CoreSession
	public IFilter createFilter(int depth) {
		if(depth<1)
			return null;
		// TODO call recursively the other Patterns and acquire their filters (depth-1)
		FilterNode node = new FilterNode();
		// TODO add Labels & Relations
		return node;
	}

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
		// TODO Auto-generated method stub
		return null;
	}
}
