package net.runeduniverse.libs.rogm.patterns;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;

@Getter
@Setter
public class RelationPattern extends AParameterHolder {

	public RelationPattern(String label) {
		super(AnonymousRelation.class, label);
	}
	public RelationPattern(Class<?> clazz, RelationshipEntity entity) {
		super(clazz, entity.label());
	}

	private NodePattern startNode;
	private NodePattern endNode;

	public class AnonymousRelation{
		
	}
}
