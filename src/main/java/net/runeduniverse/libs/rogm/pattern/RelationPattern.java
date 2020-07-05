package net.runeduniverse.libs.rogm.pattern;

import net.runeduniverse.libs.rogm.querying.IFilter;

public class RelationPattern implements IPattern {

	public static final EntityType ENITIY_TYPE = EntityType.RELATION;
	
	public RelationPattern(Class<?> type) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public EntityType getEntityType() {
		return ENITIY_TYPE;
	}

	@Override
	public IFilter createFilter(int depth) {
		// TODO Auto-generated method stub
		return null;
	}

}
