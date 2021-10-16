package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.Direction;

public class FilterNode extends AParamFilter<FilterNode> implements IFNode, IIdentified<Serializable> {

	@Getter
	Set<IFRelation> relations = new HashSet<>();
	@Getter
	private Serializable id;

	public FilterNode() {
		super.setInstance(this);
	}

	public FilterNode(Serializable id) {
		this.id = id;
		super.setInstance(this);
	}

	// Relations
	public FilterNode addRelation(IFRelation relation) {
		relations.add(relation);
		return this;
	}

	public FilterNode addRelation(FilterRelation relation, IFNode target) {
		relations.add(relation.setStart(this).setTarget(target));
		return this;
	}

	// Relations TO
	public FilterNode addRelationTo(IFNode node) {
		relations.add(new FilterRelation(this, node, Direction.OUTGOING));
		return this;
	}

	public FilterNode addRelationTo(FilterRelation relation) {
		relations.add(relation.setStart(this).setDirection(Direction.OUTGOING));
		return this;
	}

	// Relations From
	public FilterNode addRelationFrom(IFNode node) {
		relations.add(new FilterRelation(node, this, Direction.INCOMING));
		return this;
	}

	public FilterNode addRelationFrom(FilterRelation relation) {
		relations.add(relation.setTarget(this).setDirection(Direction.INCOMING));
		return this;
	}
}
