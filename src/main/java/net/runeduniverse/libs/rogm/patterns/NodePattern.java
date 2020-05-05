package net.runeduniverse.libs.rogm.patterns;

import net.runeduniverse.libs.rogm.annotations.NodeEntity;

public class NodePattern extends AParameterHolder{

	public NodePattern(Class<?> clazz, NodeEntity entity) {
		super(clazz, entity.label());
	}
}
