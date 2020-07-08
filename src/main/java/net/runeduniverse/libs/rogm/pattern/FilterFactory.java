package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IIdentified;
import net.runeduniverse.libs.rogm.querying.IOptional;
import net.runeduniverse.libs.rogm.querying.IParameterized;
import net.runeduniverse.libs.rogm.querying.IReturned;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FilterFactory {

	private final Module module;
	
	public Node createNode(Set<String> labels, List<IFilter> relations) {
		return new Node(labels, relations);
	}
	
	public Node createIdNode(Set<String> labels, List<IFilter> relations, Serializable id) {
		if(this.module.checkIdType(id.getClass()))
			return new IDNode(labels, relations, id);
		ParamNode node = new ParamNode(labels, relations);
		node.getParams().put("_id", id);
		return node;
		// TODO properly convert id to String
	}
	
	public Relation createRelation(Direction direction) {
		return new Relation(direction);
	}
	
	public Relation createIdRelation(Direction direction, Serializable id) {
		if(this.module.checkIdType(id.getClass()))
			return new IDRelation(direction, id);
		ParamRelation node = new ParamRelation(direction);
		node.getParams().put("_id", id);
		return node;
		// TODO properly convert id to String
	}
	
	@Getter @Setter
	protected abstract class Filter implements IOptional, IReturned{
		protected boolean returned = false;
		protected boolean optional = false;
	}
	
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	protected class Node extends Filter implements IFNode {
		protected Set<String> labels = new HashSet<>();
		protected List<IFilter> relations = new ArrayList<>();
	}

	@Getter
	private class IDNode extends Node implements IIdentified<Serializable> {
		private Serializable id;

		private IDNode(Set<String> labels, List<IFilter> relations, Serializable id) {
			super(labels, relations);
			this.id = id;
		}
	}

	@Getter
	private class ParamNode extends Node implements IParameterized {
		private Map<String, Object> params = new HashMap<>();

		private ParamNode(Set<String> labels, List<IFilter> relations) {
			super(labels, relations);
		}
	}

	@Data
	protected class Relation extends Filter implements IFRelation {
		protected IFilter start;
		protected IFilter target;
		protected Direction direction;
		protected Set<String> labels = new HashSet<>();

		private Relation(Direction direction) {
			this.direction = direction;
		}
		
		public void setUnsetNode(IFNode node) {
			if(this.start==null)
				this.start=node;
			if(this.target==null)
				this.target=node;
		}
	}

	@Getter
	private class IDRelation extends Relation implements IIdentified<Serializable> {
		private Serializable id;
		
		private IDRelation(Direction direction, Serializable id) {
			super(direction);
			this.id = id;
		}
	}

	@Getter
	private class ParamRelation extends Relation implements IParameterized {
		private Map<String, Object> params = new HashMap<>();

		private ParamRelation(Direction direction) {
			super(direction);
		}
	}
}
