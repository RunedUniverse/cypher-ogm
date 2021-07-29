package net.runeduniverse.libs.rogm.querying.builder;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;

@Getter
@Setter
public class RelationFilter extends AProxyFilter<RelationFilter> implements IFRelation {

	private Direction direction;
	private IFNode start;
	private IFNode target;

	public RelationFilter() {
		this.instance = this;
	}

}
