package net.runeduniverse.libs.rogm.pattern;

import net.runeduniverse.libs.rogm.querying.IFilter;

public class NodePattern implements IPattern {

	public static final EntityType ENITIY_TYPE = EntityType.NODE;
	
	public NodePattern(Class<?> type) {
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
