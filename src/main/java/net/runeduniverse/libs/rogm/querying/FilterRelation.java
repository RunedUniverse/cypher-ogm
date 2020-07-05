package net.runeduniverse.libs.rogm.querying;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.Direction;

@Getter
public class FilterRelation extends AParamFilter<FilterRelation> implements IFRelation {

	private IFilter start = null;
	private IFilter target = null;
	private Direction direction;

	public FilterRelation() {
		super.setInstance(this);
		this.direction = Direction.BIDIRECTIONAL;
	}

	public FilterRelation(Direction direction) {
		super.setInstance(this);
		this.direction = direction;
	}

	public FilterRelation(IFNode start, IFNode target, Direction direction) {
		super.setInstance(this);
		this.start = start;
		this.target = target;
		this.direction = direction;
	}

	public FilterRelation setDirection(Direction direction) {
		this.direction = direction;
		return this;
	}

	public FilterRelation setStart(IFNode node) {
		this.start = node;
		return this;
	}

	public FilterRelation setTarget(IFNode node) {
		this.target = node;
		return this;
	}

}
