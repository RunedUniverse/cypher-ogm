package net.runeduniverse.libs.rogm.querying;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.runeduniverse.libs.rogm.querying.FRelation.Direction;

public class FilterNode extends AParamFilter<FilterNode> implements FNode {
	
	@Getter
	List<Filter> relations = new ArrayList<>();
	
	public FilterNode() {
		super.setInstance(this);
	}
	
	// Relations
	public FilterNode addRelation(FRelation relation) {
		relations.add(relation);
		return this;
	}
	public FilterNode addRelation(IdentifiedFilter<?> relation) {
		relations.add(relation);
		return this;
	}
	public FilterNode addRelation(FilterRelation relation, FNode target) {
		relations.add(relation.setStart(this).setTarget(target));
		return this;
	}
	public FilterNode addRelation(FilterRelation relation, IdentifiedFilter<?> target) {
		relations.add(relation.setStart(this).setTarget(target));
		return this;
	}
	// Relations TO
	public FilterNode addRelationTo(FNode node) {
		relations.add(new FilterRelation(this, node, Direction.OUTGOING));
		return this;
	}
	public FilterNode addRelationTo(IdentifiedFilter<?> idNode) {
		relations.add(new FilterRelation(this, null, Direction.OUTGOING).setTarget(idNode));
		return this;
	}
	public FilterNode addRelationTo(FilterRelation relation) {
		relations.add(relation.setStart(this).setDirection(Direction.OUTGOING));
		return this;
	}
	// Relations From
	public FilterNode addRelationFrom(FNode node) {
		relations.add(new FilterRelation(node, this, Direction.INCOMING));
		return this;
	}
	public FilterNode addRelationFrom(IdentifiedFilter<?> idNode) {
		relations.add(new FilterRelation(null, this, Direction.INCOMING).setStart(idNode));
		return this;
	}
	public FilterNode addRelationFrom(FilterRelation relation) {
		relations.add(relation.setTarget(this).setDirection(Direction.INCOMING));
		return this;
	}
}
