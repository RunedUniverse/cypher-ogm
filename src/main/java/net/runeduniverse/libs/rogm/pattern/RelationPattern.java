package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;

import net.runeduniverse.libs.rogm.querying.FilterRelation;
import net.runeduniverse.libs.rogm.querying.IDFilterRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class RelationPattern implements IPattern {

	public static final EntityType ENITIY_TYPE = EntityType.RELATION;
	private final PatternStorage storage;
	private final Class<?> type;
	private Field idField;

	public RelationPattern(PatternStorage storage, Class<?> type) {
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
		FilterRelation relation = new FilterRelation();
		// TODO add Labels & Relations
		return relation;
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
			return new IDFilterRelation<ID>(id);
		}
		// ParamFilter
		return new FilterRelation().addParam("_id", id);
	}

}
