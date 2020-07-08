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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.lang.Language.DataFilter;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IIdentified;
import net.runeduniverse.libs.rogm.querying.ILabeled;
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
		if (this.module.checkIdType(id.getClass()))
			return new IDNode(labels, relations, id);
		ParamNode node = new ParamNode(labels, relations);
		node.getParams().put("_id", id);
		return node;
		// TODO properly convert id to String
	}

	public DataNode createIdDataNode(Set<String> labels, List<IFilter> relations, Serializable id, Object data) {
		if (this.module.checkIdType(id.getClass()))
			return new IDDataNode(labels, relations, id, data);
		ParamDataNode node = new ParamDataNode(labels, relations, data);
		node.getParams().put("_id", id);
		return node;
		// TODO properly convert id to String
	}

	public DataNode createDataNode(Set<String> labels, List<IFilter> relations, Object data) {
		return new ParamDataNode(labels, relations, data);
	}

	public Relation createRelation(Direction direction) {
		return new Relation(direction);
	}

	public Relation createIdRelation(Direction direction, Serializable id) {
		if (this.module.checkIdType(id.getClass()))
			return new IDRelation(direction, id);
		ParamRelation node = new ParamRelation(direction);
		node.getParams().put("_id", id);
		return node;
		// TODO properly convert id to String
	}

	public DataRelation createIdDataRelation(Direction direction, Serializable id, Object data) {
		if (this.module.checkIdType(id.getClass()))
			return new IDDataRelation(direction, id, data);
		ParamDataRelation node = new ParamDataRelation(direction, data);
		node.getParams().put("_id", id);
		return node;
		// TODO properly convert id to String
	}

	public DataRelation createDataRelation(Direction direction, Object data) {
		return new ParamDataRelation(direction, data);
	}

	
	protected interface DataNode extends DataFilter, ILabeled, IReturned, IFNode{
		void setFilterType(FilterType type);
		void setReturned(boolean returned);
	}
	protected interface DataRelation extends DataFilter, ILabeled, IReturned, IFRelation{
		void setFilterType(FilterType type);
		void setReturned(boolean returned);
		void setStart(IFilter start);
		void setTarget(IFilter target);
	}
	
	
	@Getter
	@Setter
	protected abstract class Filter implements IOptional, IReturned {
		protected boolean returned = false;
		protected boolean optional = false;
		protected FilterType filterType = FilterType.MATCH;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	protected class Node extends Filter implements IFNode {
		protected Set<String> labels = new HashSet<>();
		protected List<IFilter> relations = new ArrayList<>();
	}

	@Getter
	private class IDNode extends Node implements IIdentified<Serializable> {
		protected Serializable id;

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

	@Getter
	private class IDDataNode extends IDNode implements DataNode {
		private Object data;

		private IDDataNode(Set<String> labels, List<IFilter> relations, Serializable id, Object data) {
			super(labels, relations, id);
			this.data = data;
		}
	}

	@Getter
	private class ParamDataNode extends ParamNode implements DataNode {
		private Object data;

		private ParamDataNode(Set<String> labels, List<IFilter> relations, Object data) {
			super(labels, relations);
			this.data = data;
		}
	}

	@Getter
	@Setter
	protected class Relation extends Filter implements IFRelation {
		protected IFilter start;
		protected IFilter target;
		protected Direction direction;
		protected Set<String> labels = new HashSet<>();

		private Relation(Direction direction) {
			this.direction = direction;
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

	@Getter
	private class IDDataRelation extends IDRelation implements DataRelation {
		private Object data;

		private IDDataRelation(Direction direction, Serializable id, Object data) {
			super(direction, id);
			this.data = data;
		}
	}

	@Getter
	private class ParamDataRelation extends ParamRelation implements DataRelation {
		private Object data;

		private ParamDataRelation(Direction direction, Object data) {
			super(direction);
			this.data = data;
		}
	}
}
