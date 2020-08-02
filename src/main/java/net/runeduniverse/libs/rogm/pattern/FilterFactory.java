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
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.querying.*;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FilterFactory {

	private final Module module;

	public Node createNode(Set<String> labels, List<IFRelation> relations) {
		return new Node(labels, relations);
	}

	public Node createIdNode(Set<String> labels, List<IFRelation> relations, Serializable id, IConverter<?> converter) {
		if (this.module.checkIdType(id.getClass()))
			return new Node(id, labels, relations);
		Node node = new Node(labels, relations);
		node.getParams().put(this.module.getIdAlias(), converter.toProperty(id));
		return node;
	}

	public DataNode createIdDataNode(Set<String> labels, List<IFRelation> relations, Serializable id,
			IConverter<?> converter, Object data, boolean persist) {
		if (this.module.checkIdType(id.getClass()))
			return new DataNode(data, id, labels, relations, persist);
		DataNode node = new DataNode(data, labels, relations, persist);
		node.getParams().put(this.module.getIdAlias(), converter.toProperty(id));
		return node;
	}

	public DataNode createDataNode(Set<String> labels, List<IFRelation> relations, Object data, boolean persist) {
		return new DataNode(data, labels, relations, persist);
	}

	public Relation createRelation(Direction direction) {
		return new Relation(direction);
	}

	public Relation createIdRelation(Direction direction, Serializable id, IConverter<?> converter) {
		if (this.module.checkIdType(id.getClass()))
			return new Relation(id, direction);
		Relation node = new Relation(direction);
		node.getParams().put(this.module.getIdAlias(), converter.toProperty(id));
		return node;
	}

	public IDataRelation createIdDataRelation(Direction direction, Serializable id, IConverter<?> converter,
			Object data) {
		if (this.module.checkIdType(id.getClass()))
			return new DataRelation(data, id, direction);
		DataRelation node = new DataRelation(data, direction);
		node.getParams().put(this.module.getIdAlias(), converter.toProperty(id));
		return node;
	}

	public IDataRelation createDataRelation(Direction direction, Object data) {
		return new DataRelation(data, direction);
	}

	// from node deletion process effected relations
	public IFRelation createEffectedFilter(Serializable id) {
		Relation relation = new Relation(Direction.BIDIRECTIONAL);
		relation.setStart(new Node(id, null, null));
		Node target = new Node(null, null);
		target.setReturned(true);
		relation.setTarget(target);
		relation.setReturned(true);
		return relation;
	}

	protected interface IDataNode extends IFNode, IDataContainer, ILabeled, IReturned {
		void setFilterType(FilterType type);

		void setReturned(boolean returned);
	}

	protected interface IDataRelation extends IFRelation, IDataContainer, ILabeled, IReturned {
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
	protected class Node extends Filter implements IFNode, ILazyLoading {
		protected Set<String> labels = new HashSet<>();
		protected List<IFRelation> relations = new ArrayList<>();
		@Setter
		protected boolean lazy = false;

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
	protected class DataNode extends Node implements IDataNode {
		private Object data;
		private boolean persist;
		@Setter
		protected boolean readonly = false;

		private DataNode(Object data, Serializable id, Set<String> labels, List<IFRelation> relations,
				boolean persist) {
			super(id, labels, relations);
			this.data = data;
			this.persist = persist;
		}

		private DataNode(Object data, Set<String> labels, List<IFRelation> relations, boolean persist) {
			super(labels, relations);
			this.data = data;
			this.persist = persist;
		}

		@Override
		public boolean persist() {
			return persist;
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
		protected boolean readonly = false;

		private DataRelation(Object data, Direction direction) {
			super(direction);
			this.data = data;
		}

		private DataRelation(Object data, Serializable id, Direction direction) {
			super(id, direction);
			this.data = data;
		}

		@Override
		public boolean persist() {
			return false;
		}
	}
}
