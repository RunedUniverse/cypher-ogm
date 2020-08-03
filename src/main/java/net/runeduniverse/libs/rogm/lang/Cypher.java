package net.runeduniverse.libs.rogm.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.IBuffer.LoadState;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.Data;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IPatternContainer;
import net.runeduniverse.libs.rogm.querying.*;
import net.runeduniverse.libs.rogm.util.*;

public class Cypher implements Language {

	@Override
	public Instance build(Parser.Instance parser, Module module) {
		return new CypherInstance(parser, module);
	}

	@RequiredArgsConstructor
	public class CypherInstance implements Instance {
		private final Parser.Instance parser;
		private final Module module;

		@Override
		public ILoadMapper load(IFilter filter) throws Exception {
			DataMap<IFilter, String, FilterStatus> map = new DataHashMap<>();
			return new Mapper(filter, _load(map, filter, true), map);
		}

		public String _load(DataMap<IFilter, String, FilterStatus> map, IFilter filter, boolean all) throws Exception {
			if (filter == null)
				return null;
			StringVariableGenerator gen = new StringVariableGenerator();

			_parse(map, filter, gen, false);

			StringBuilder qry = _select(map).append("RETURN ");
			List<String> rt = new ArrayList<>();

			map.forEach((f, c) -> {
				if (f instanceof IReturned && ((IReturned) f).isReturned()) {
					rt.add(_returnId(c));
					if (all)
						rt.add(_returnLabel(c, f instanceof IFNode) + ',' + c);
				}
			});

			if (rt.isEmpty()) {
				String c = map.get(filter);
				qry.append(_returnId(c));
				if (all)
					qry.append(',' + _returnLabel(c, filter instanceof IFNode) + ',' + c);
			} else
				qry.append(String.join(",", rt));
			return qry.append(';').toString();
		}

		@Override
		public ISaveMapper save(IDataContainer node, Set<IFilter> filter) throws Exception {
			DataMap<IFilter, String, FilterStatus> map = new DataHashMap<>();
			StringVariableGenerator gen = new StringVariableGenerator();
			_parse(map, node, gen, false);

			StringBuilder qry = _select(map);

			List<String> st = new ArrayList<>();
			List<String> rt = new ArrayList<>();

			map.forEach((f, c) -> {
				rt.add(_returnId(c));
				try {
					IDataContainer d = (IDataContainer) f;
					if (d.isReadonly())
						return;
					if (d.getData() != null)
						st.add(c + '=' + parser.serialize(d.getData()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			// SET + RETURN
			return new Mapper(this, node, qry.append("SET ").append(String.join(",", st)).append("\nRETURN ")
					.append(String.join(",", rt)).append(';').toString(), filter, map);
		}

		@Override
		public IDeleteMapper delete(IFilter filter, IFRelation relation) throws Exception {
			DataMap<IFilter, String, FilterStatus> map = new DataHashMap<>();
			StringVariableGenerator gen = new StringVariableGenerator();
			_parse(map, filter, gen, false);

			StringBuilder qryBuilder = _select(map);
			List<String> dl = new ArrayList<>();

			map.forEach((f, c) -> {
				if (f instanceof IReturned && ((IReturned) f).isReturned())
					if (f instanceof IFNode)
						dl.add("DETACH DELETE " + c);
					else
						dl.add("DELETE " + c);
			});

			if (!dl.isEmpty())
				qryBuilder.append(String.join("\n", dl));

			DataMap<IFilter, String, FilterStatus> effectedMap = new DataHashMap<>();
			return new Mapper(qryBuilder.append(';').toString(), this._load(effectedMap, relation, false), effectedMap);
		}

		@Override
		public String deleteRelations(Collection<String> ids) {
			return "UNWIND [" + String.join(",", ids) + "] AS id MATCH ()-[a]-() WHERE id(a) = id DELETE a;";
		}

		private String _returnId(String code) {
			return "id(" + code + ") AS id_" + code + ',' + code + ".`" + this.module.getIdAlias() + "` AS eid_" + code;
		}

		private String _returnLabel(String code, boolean isNode) {
			if (isNode)
				return "labels(" + code + ") AS labels_" + code;
			return "type(" + code + ") AS labels_" + code;
		}

		private void _parse(DataMap<IFilter, String, FilterStatus> map, IFilter filter, StringVariableGenerator gen,
				boolean included) throws Exception {
			if (map.containsKey(filter)) {
				if (included)
					map.setData(filter, FilterStatus.PRE_PRINTED);
				return;
			}
			if (filter == null) {
				map.put(filter, "", FilterStatus.INITIALIZED);
				return;
			}
			map.put(filter, gen.nextVal(), included ? FilterStatus.PRE_PRINTED : FilterStatus.INITIALIZED);

			if (filter instanceof IFNode) {
				_checkIdType(filter);
				IFNode node = (IFNode) filter;
				if (node.getRelations() != null)
					for (IFilter f : node.getRelations())
						_parse(map, f, gen, false);
			} else if (filter instanceof IFRelation) {
				_checkIdType(filter);
				_parse(map, ((IFRelation) filter).getStart(), gen, true);
				_parse(map, ((IFRelation) filter).getTarget(), gen, true);
			} else
				throw new Exception("IFilter<" + filter.toString() + "> not supported");
		}

		private StringBuilder _select(DataMap<IFilter, String, FilterStatus> map) throws Exception {
			StringBuilder matchBuilder = new StringBuilder();
			StringBuilder createBuilder = new StringBuilder();
			StringBuilder mergeBuilder = new StringBuilder();
			StringBuilder relMergeBuilder = new StringBuilder();
			StringBuilder optionalMatchBuilder = new StringBuilder();
			List<String> where = new ArrayList<>();

			map.forEach((f, code, modifier) -> {
				StringBuilder activeBuilder = null;
				boolean optional = false;
				short isMerge = _isMerge(f);

				if (f != null && f.getFilterType() == FilterType.MATCH && f instanceof IOptional
						&& ((IOptional) f).isOptional())
					optional = true;

				_where(map, where, f, code, modifier);

				if (f == null || modifier.equals(FilterStatus.PRINTED)
						|| optional && modifier.equals(FilterStatus.PRE_PRINTED))
					return;

				if (optional)
					activeBuilder = optionalMatchBuilder.append("OPTIONAL ");
				else if (isMerge == 0)
					activeBuilder = relMergeBuilder;
				else if (isMerge == 1)
					activeBuilder = mergeBuilder;
				else if (f.getFilterType() == FilterType.CREATE)
					activeBuilder = createBuilder;
				else
					activeBuilder = matchBuilder;

				activeBuilder.append(_prefix(f, -1 < isMerge));
				if (f instanceof IFNode) {
					if (!(modifier.equals(FilterStatus.PRE_PRINTED) && optional)) {
						activeBuilder.append(_filterToString(map, f, true, false, false));
						map.setData(f, FilterStatus.PRINTED);
					}

				} else if (f instanceof IFRelation) {
					activeBuilder.append(_translateRelation(map, (IFRelation) f, optional, -1 < isMerge).toString());
				} else
					activeBuilder.append("()");
				activeBuilder.append('\n');
			});
			if (!where.isEmpty())
				matchBuilder.append("WHERE " + String.join(" AND ", where) + '\n');
			return matchBuilder.append(createBuilder).append(mergeBuilder).append(relMergeBuilder)
					.append(optionalMatchBuilder);
		}

		private short _isMerge(IFilter filter) {
			if (filter == null)
				return -1;
			switch (filter.getFilterType()) {
			case CREATE:
			case UPDATE:
				if (filter instanceof IFRelation && !IIdentified.identify(filter))
					return 0;
				if (filter instanceof IParameterized
						&& ((IParameterized) filter).getParams().containsKey(this.module.getIdAlias()))
					return 1;
			default:
				return -1;
			}
		}

		private String _prefix(IFilter filter, Boolean isMerge) {
			if (isMerge)
				return "MERGE ";

			switch (filter.getFilterType()) {
			case CREATE:
				return "CREATE ";
			case UPDATE:
			case MATCH:
			case DELETE:
			default:
				return "MATCH ";
			}
		}

		private void _checkIdType(IFilter filter) throws Exception {
			Class<?> clazz = IIdentified.getIdType(filter);
			if (clazz != null && !this.module.checkIdType(clazz))
				throw new Exception("IFilter ID <" + clazz + "> not supported");
		}

		private void _where(DataMap<IFilter, String, FilterStatus> map, List<String> where, IFilter f, String code,
				FilterStatus modifier) {
			if (f == null || !(f.getFilterType() != FilterType.CREATE && IIdentified.identify(f)
					&& !modifier.equals(FilterStatus.EXTENSION_PRINTED)))
				return;
			IIdentified<?> i = (IIdentified<?>) f;
			where.add("id(" + code + ")=" + ((Number) i.getId()).longValue());
			map.setData(f, FilterStatus.EXTENSION_PRINTED);
		}

		private StringBuilder _translateRelation(DataMap<IFilter, String, FilterStatus> map, IFRelation rel,
				boolean optional, boolean isMerge) {
			StringBuilder matchLine = new StringBuilder(_filterToString(map, rel.getStart(), true, optional, isMerge));

			switch (rel.getDirection()) {
			case INCOMING:
				matchLine.append("<-");
				break;
			case OUTGOING:
			case BIDIRECTIONAL:
				matchLine.append('-');
			}

			matchLine.append(_filterToString(map, rel, false, false, false));

			switch (rel.getDirection()) {
			case OUTGOING:
				matchLine.append("->");
				break;
			case INCOMING:
			case BIDIRECTIONAL:
				matchLine.append('-');
			}

			return matchLine.append(_filterToString(map, rel.getTarget(), true, optional, isMerge));
		}

		private String _filterToString(DataMap<IFilter, String, FilterStatus> map, IFilter filter, boolean isNode,
				boolean optional, boolean skipData) {
			StringBuilder builder = new StringBuilder(map.get(filter));

			if (!skipData && !map.getData(filter).equals(FilterStatus.PRINTED)) {
				// PRINT LABELS
				if (filter instanceof ILabeled) {
					ILabeled holder = (ILabeled) filter;
					if (holder.getLabels() != null)
						for (String label : holder.getLabels())
							builder.append(':' + label.replace(' ', '_'));
				}
				// PRINT DATA
				if (filter instanceof IParameterized) {
					IParameterized holder = (IParameterized) filter;
					if (!holder.getParams().isEmpty())
						try {
							builder.append(' ' + parser.serialize(holder.getParams()));
						} catch (Exception e) {
							e.printStackTrace();
						}
				} else if (filter instanceof IDataContainer) {
					IDataContainer holder = (IDataContainer) filter;
					try {
						builder.append(' ' + parser.serialize(holder.getData()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// disable future param printing
				if (filter != null && (optional || filter.getFilterType() == FilterType.CREATE))
					map.setData(filter, FilterStatus.PRE_PRINTED);
				else
					map.setData(filter, FilterStatus.PRINTED);
			}

			if (isNode)
				return '(' + builder.toString() + ')';
			else
				return '[' + builder.toString() + ']';
		}
	}

	@AllArgsConstructor
	protected static class FilterStatus {
		public static final FilterStatus INITIALIZED = new FilterStatus(1);
		public static final FilterStatus PRE_PRINTED = new FilterStatus(2);
		public static final FilterStatus PRINTED = new FilterStatus(3);
		public static final FilterStatus EXTENSION_PRINTED = new FilterStatus(4);

		@Getter
		private int status = 0;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FilterStatus)
				return this.status >= ((FilterStatus) obj).getStatus();
			return super.equals(obj);
		}
	}

	protected static class Mapper implements Language.ILoadMapper, Language.ISaveMapper, Language.IDeleteMapper {

		private CypherInstance cypher;
		private IFilter primary;
		private String qry;
		private String effectedQry;
		private Collection<IFilter> effectedQrys;
		private DataMap<IFilter, String, FilterStatus> map;
		// working data
		private Collection<String> persistIds = new HashSet<>();

		protected Mapper(IFilter primaryFilter, String qry, DataMap<IFilter, String, FilterStatus> map) {
			this.primary = primaryFilter;
			this.qry = qry;
			this.map = map;
		}

		protected Mapper(CypherInstance cypher, IFilter primaryFilter, String qry, Collection<IFilter> effectedQrys,
				DataMap<IFilter, String, FilterStatus> map) {
			this.cypher = cypher;
			this.primary = primaryFilter;
			this.qry = qry;
			this.effectedQrys = effectedQrys;
			this.map = map;
		}

		protected Mapper(String qry, String effectedQry, DataMap<IFilter, String, FilterStatus> map) {
			this.qry = qry;
			this.effectedQry = effectedQry;
			this.map = map;
		}

		@Override
		public String qry() {
			return this.qry;
		}

		@Override
		public String effectedQry() {
			return this.effectedQry;
		}

		@Override
		public <ID extends Serializable> void updateObjectIds(IBuffer buffer, Map<String, ID> ids,
				LoadState loadState) {
			this.map.forEach((filter, code) -> {
				if (filter instanceof IFRelation) {
					Object s = ids.get("id_" + code);
					if (s != null)
						this.persistIds.add(s.toString());
				}
				if (filter instanceof IDataContainer) {
					Object data = ((IDataContainer) filter).getData();
					if (data == null)
						return;
					LoadState fLoadState = loadState;
					if (filter instanceof IFRelation)
						fLoadState = LoadState.COMPLETE;
					try {
						buffer.updateEntry(ids.get("id_" + code), ids.get("eid_" + code), data, fLoadState);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

		@Override
		public Collection<String> reduceIds(IBuffer buffer, Module.Instance<?> module) throws Exception {
			Collection<String> delIds = new HashSet<>();

			for (IFilter qryFilter : effectedQrys) {
				DataMap<IFilter, String, FilterStatus> effectedMap = new DataHashMap<>();

				for (Map<String, Object> ids : module.query(cypher._load(effectedMap, qryFilter, false))) {
					effectedMap.forEach((filter, code) -> {
						if (!(filter instanceof IFNode))
							return;

						for (IFRelation rel : ((IFNode) filter).getRelations()) {
							Object s = ids.get("id_" + effectedMap.get(rel));
							if (s == null)
								continue;
							delIds.add(s.toString());
						}
					});
				}
			}

			delIds.removeAll(persistIds);
			return delIds;
		}

		@Override
		public IPattern.IDataRecord parseDataRecord(List<Map<String, Data>> records) {
			/*
			 * List => 1 Map per Record-line Map => key = a - value = all data from a
			 */
			Set<Serializable> ids = new HashSet<>();
			List<Set<IPattern.IData>> recordData = new ArrayList<>();

			for (Map<String, Data> record : records) {
				ids.add(record.get(this.map.get(this.primary)).getId());

				Set<IPattern.IData> set = new HashSet<IPattern.IData>();
				recordData.add(set);

				for (IFilter filter : this.map.keySet()) {
					Module.Data data = record.get(this.map.get(filter));
					if (data.getId() == null)
						continue;
					set.add(new PData(data, filter));
				}
			}

			return new IPattern.IDataRecord() {
				public IPattern.IPatternContainer getPrimaryFilter() {
					return (IPatternContainer) primary;
				}

				@Override
				public Set<Serializable> getIds() {
					return ids;
				}

				@Override
				public List<Set<IPattern.IData>> getData() {
					return recordData;
				}
			};
		}

		@Override
		public void updateBuffer(IBuffer buffer, Serializable deletedId, List<Map<String, Object>> effectedIds) {
			for (Map<String, Object> ids : effectedIds)
				this.map.forEach((f, c) -> {
					if (!(f instanceof IFRelation))
						return;
					IFRelation rel = (IFRelation) f;
					String code = "id_" + this.map.get(rel.getStart());
					if (!ids.containsKey(code))
						code = "id_" + this.map.get(rel.getTarget());
					buffer.eraseRelations(deletedId, (Serializable) ids.get("id_" + this.map.get(f)),
							(Serializable) ids.get(code));
				});
		}

		@Override
		public String toString() {
			return this.qry;
		}
	}

	@Getter
	protected static class PData implements IPattern.IData {
		private Serializable id;
		@Setter
		private Serializable entityId;
		private Set<String> labels;
		private String data;
		private IFilter filter;

		protected PData(Module.Data data, IFilter filter) {
			this.id = data.getId();
			this.entityId = data.getEntityId();
			this.labels = data.getLabels();
			this.data = data.getData();
			this.filter = filter;
		}

		@Override
		public String toString() {
			return "PDATA filter:<" + filter.getClass().getSimpleName() + "> id<" + id + "> eid<" + entityId
					+ "> labels<" + labels + "> data<" + data + ">";
		}
	}
}
