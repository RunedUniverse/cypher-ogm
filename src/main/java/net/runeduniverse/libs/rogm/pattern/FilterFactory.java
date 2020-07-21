package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.lang.Language.IDataFilter;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.querying.*;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FilterFactory {

	private final Module module;

	public Node createNode(Set<String> labels, List<IFRelation> relations) {
		return new Node(labels, relations);
	}

	public Node createIdNode(Set<String> labels, List<IFRelation> relations, Serializable id) {
		if (this.module.checkIdType(id.getClass()))
			return new Node(id, labels, relations);
		Node node = new Node(labels, relations);
		node.getParams().put(this.module.getIdAlias(), id);
		return node;
		// TODO properly convert id to String
	}

	public IDataNode createIdDataNode(Set<String> labels, List<IFRelation> relations, Serializable id, Object data) {
		if (this.module.checkIdType(id.getClass()))
			return new DataNode(data, id, labels, relations);
		DataNode node = new DataNode(data, labels, relations);
		node.getParams().put(this.module.getIdAlias(), id);
		return node;
		// TODO properly convert id to String
	}

	public IDataNode createDataNode(Set<String> labels, List<IFRelation> relations, Object data) {
		return new DataNode(data, labels, relations);
	}

	public Relation createRelation(Direction direction) {
		return new Relation(direction);
	}

	public Relation createIdRelation(Direction direction, Serializable id) {
		if (this.module.checkIdType(id.getClass()))
			return new Relation(id, direction);
		Relation node = new Relation(direction);
		node.getParams().put("id", id);
		return node;
		// TODO properly convert id to String
	}

	public IDataRelation createIdDataRelation(Direction direction, Serializable id, Object data) {
		if (this.module.checkIdType(id.getClass()))
			return new DataRelation(data, id, direction);
		DataRelation node = new DataRelation(data, direction);
		node.getParams().put("id", id);
		return node;
		// TODO properly convert id to String
	}

	public IDataRelation createDataRelation(Direction direction, Object data) {
		return new DataRelation(data, direction);
	}

	protected interface IDataNode extends IFNode, IDataFilter, ILabeled, IReturned {
		void setFilterType(FilterType type);

		void setReturned(boolean returned);
	}

	protected interface IDataRelation extends IFRelation, IDataFilter, ILabeled, IReturned {
		void setFilterType(FilterType type);

		void setReturned(boolean returned);

		void setStart(IFNode start);

		void setTarget(IFNode target);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	protected abstract class Filter
			implements IIdentified<Serializable>, IParameterized, IOptional, IReturned, IPattern.IPatternContainer {
		protected Serializable id;
		protected Map<String, Object> params = new HashMap<>();
		protected IPattern pattern = null;
		protected boolean returned = false;
		protected boolean optional = false;
		protected FilterType filterType = FilterType.MATCH;

		protected Filter(Serializable id) {
			this.id = id;
		}
	}

	@Getter
	protected class Node extends Filter implements IFNode {
		protected Set<String> labels = new HashSet<>();
		protected List<IFRelation> relations = new ArrayList<>();

		private Node(Set<String> labels, List<IFRelation> relations) {
			this.labels = labels;
			this.relations = relations;
		}

		private Node(Serializable id, Set<String> labels, List<IFRelation> relations) {
			super(id);
			this.labels = labels;
			this.relations = relations;
		}
	}

	@Getter
	private class DataNode extends Node implements IDataNode {
		private Object data;

		private DataNode(Object data, Serializable id, Set<String> labels, List<IFRelation> relations) {
			super(id, labels, relations);
			this.data = data;
		}

		private DataNode(Object data, Set<String> labels, List<IFRelation> relations) {
			super(labels, relations);
			this.data = data;
		}
	}

	@Getter
	@Setter
	protected class Relation extends Filter implements IFRelation {
		protected IFNode start;
		protected IFNode target;
		protected Direction direction;
		protected Set<String> labels = new HashSet<>();

		private Relation(Direction direction) {
			this.direction = direction;
		}

		private Relation(Serializable id, Direction direction) {
			super(id);
			this.direction = direction;
		}
	}

	@Getter
	private class DataRelation extends Relation implements IDataRelation {
		private Object data;

		private DataRelation(Object data, Direction direction) {
			super(direction);
			this.data = data;
		}

		private DataRelation(Object data, Serializable id, Direction direction) {
			super(id, direction);
			this.data = data;
		}
	}
}
