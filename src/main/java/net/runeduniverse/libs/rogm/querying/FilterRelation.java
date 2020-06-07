package net.runeduniverse.libs.rogm.querying;

import lombok.Getter;

@Getter
public class FilterRelation extends AParamFilter<FilterRelation> implements FRelation {

	private Filter start;
	private Filter target;
	private Direction direction;

	public FilterRelation() {
		super.setInstance(this);
		this.direction = Direction.BIDIRECTIONAL;
	}

	public FilterRelation(Direction direction) {
		super.setInstance(this);
		this.direction = direction;
	}

	public FilterRelation(FNode start, FNode target, Direction direction) {
		super.setInstance(this);
		this.start = start;
		this.target = target;
		this.direction = direction;
	}

	public FilterRelation setDirection(Direction direction) {
		this.direction = direction;
		return this;
	}

	public FilterRelation setStart(FNode node) {
		this.start = node;
		return this;
	}

	public FilterRelation setTarget(FNode node) {
		this.target = node;
		return this;
	}

	public FilterRelation setStart(IdentifiedFilter node) {
		this.start = node;
		return this;
	}

	public FilterRelation setTarget(IdentifiedFilter node) {
		this.target = node;
		return this;
	}

}
