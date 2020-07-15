package net.runeduniverse.libs.rogm.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.Data;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;
import net.runeduniverse.libs.rogm.querying.*;
import net.runeduniverse.libs.rogm.util.*;

public class Cypher implements Language {

	@Override
	public Mapper buildQuery(IFilter filter, Parser parser) throws Exception {
		DataMap<IFilter, String, FilterStatus> map = new DataHashMap<>();
		StringVariableGenerator gen = new StringVariableGenerator();
		_parse(map, filter, gen, false);

		StringBuilder qry = _select(map, parser).append("RETURN ");
		List<String> rt = new ArrayList<>();

		map.forEach((f, c) -> {
			if (f instanceof IReturned && ((IReturned) f).isReturned())
				rt.add(_returnId(c) + ", " + _returnLabel(c, f instanceof IFNode) + ", " + c);
		});

		if (rt.isEmpty()) {
			String c = map.get(filter);
			qry.append(_returnId(c) + ", " + _returnLabel(c, filter instanceof IFNode) + ", " + c);
		} else
			qry.append(String.join(", ", rt));

		return new Mapper(qry.append(';').toString(), map);
	}

	@Override
	public Mapper buildSave(DataFilter node, Parser parser) throws Exception {
		DataMap<IFilter, String, FilterStatus> map = new DataHashMap<>();
		StringVariableGenerator gen = new StringVariableGenerator();
		_parse(map, node, gen, false);

		StringBuilder qry = _select(map, parser);

		List<String> st = new ArrayList<>();
		List<String> rt = new ArrayList<>();

		map.forEach((f, c) -> {
			try {
				DataFilter d = (DataFilter) f;
				if (d.getData() != null)
					st.add(c + '=' + parser.serialize(d.getData()));
				rt.add(_returnId(c));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// SET + RETURN
		return new Mapper(qry.append("SET ").append(String.join(", ", st)).append("\nRETURN ")
				.append(String.join(", ", rt)).append(';').toString(), map);
	}

	private String _returnId(String code) {
		return "id(" + code + ") as id_" + code;
	}

	private String _returnLabel(String code, boolean isNode) {
		if (isNode)
			return "labels(" + code + ") as labels_" + code;
		return "type(" + code + ") as labels_" + code;
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
			IIdentified.checkType(Number.class, filter);
			for (IFilter f : ((IFNode) filter).getRelations())
				_parse(map, f, gen, false);
		} else if (filter instanceof IFRelation) {
			IIdentified.checkType(Number.class, filter);
			_parse(map, ((IFRelation) filter).getStart(), gen, true);
			_parse(map, ((IFRelation) filter).getTarget(), gen, true);
		} else
			throw new Exception("IFilter<" + filter.toString() + "> not supported");
	}

	private StringBuilder _select(DataMap<IFilter, String, FilterStatus> map, Parser parser) throws Exception {
		StringBuilder matchBuilder = new StringBuilder();
		StringBuilder mergeBuilder = new StringBuilder();
		StringBuilder optionalMatchBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();

		map.forEach((f, code, modifier) -> {
			StringBuilder activeBuilder = null;
			boolean optional = false;
			boolean isMerge = _isMerge(f);

			if (f != null && f.getFilterType() == FilterType.MATCH && f instanceof IOptional
					&& ((IOptional) f).isOptional())
				optional = true;

			if (f == null || modifier.equals(FilterStatus.PRINTED)
					|| optional && modifier.equals(FilterStatus.PRE_PRINTED))
				return;

			if (optional)
				activeBuilder = optionalMatchBuilder.append("OPTIONAL ");
			else if (isMerge)
				activeBuilder = mergeBuilder;
			else
				activeBuilder = matchBuilder;

			activeBuilder.append(_prefix(f));
			if (f instanceof IFNode) {
				_where(map, whereBuilder, f, code, modifier);

				if (!(modifier.equals(FilterStatus.PRE_PRINTED) && optional)) {
					activeBuilder.append(_filterToString(map, f, true, parser, false, false));
					map.setData(f, FilterStatus.PRINTED);
				}

			} else if (f instanceof IFRelation) {
				_where(map, whereBuilder, f, code, modifier);

				activeBuilder.append(_translateRelation(map, parser, (IFRelation) f, optional, isMerge).toString());
			} else
				activeBuilder.append("()");
			activeBuilder.append('\n');
		});
		return matchBuilder.append(mergeBuilder).append(whereBuilder).append(optionalMatchBuilder);
	}

	private boolean _isMerge(IFilter filter) {
		if (filter == null)
			return false;
		switch (filter.getFilterType()) {
		case CREATE:
		case UPDATE:
			if (filter instanceof IFRelation)
				return true;
		default:
			return false;
		}
	}

	private String _prefix(IFilter filter) {
		if (_isMerge(filter))
			return "MERGE ";

		switch (filter.getFilterType()) {
		case CREATE:
			return "CREATE ";
		case UPDATE:
		case MATCH:
		default:
			return "MATCH ";
		}
	}

	private void _where(DataMap<IFilter, String, FilterStatus> map, StringBuilder builder, IFilter f, String code,
			FilterStatus modifier) {
		if (!(f.getFilterType() != FilterType.CREATE && IIdentified.identify(f)
				&& !modifier.equals(FilterStatus.EXTENSION_PRINTED)))
			return;
		IIdentified<?> i = (IIdentified<?>) f;
		builder.append("WHERE id(" + code + ")=" + ((Number) i.getId()).longValue() + "\n");
		map.setData(f, FilterStatus.EXTENSION_PRINTED);
	}

	private StringBuilder _translateRelation(DataMap<IFilter, String, FilterStatus> map, Parser parser, IFRelation rel,
			boolean optional, boolean isMerge) {
		StringBuilder matchLine = new StringBuilder(
				_filterToString(map, rel.getStart(), true, parser, optional, isMerge));

		switch (rel.getDirection()) {
		case INCOMING:
			matchLine.append("<-");
			break;
		case OUTGOING:
		case BIDIRECTIONAL:
			matchLine.append('-');
		}

		matchLine.append(_filterToString(map, rel, false, parser, false, false));

		switch (rel.getDirection()) {
		case OUTGOING:
			matchLine.append("->");
			break;
		case INCOMING:
		case BIDIRECTIONAL:
			matchLine.append('-');
		}

		return matchLine.append(_filterToString(map, rel.getTarget(), true, parser, optional, isMerge));
	}

	private String _filterToString(DataMap<IFilter, String, FilterStatus> map, IFilter filter, boolean isNode,
			Parser parser, boolean optional, boolean skipData) {
		StringBuilder builder = new StringBuilder(map.get(filter));

		if (!skipData && !map.getData(filter).equals(FilterStatus.PRINTED)) {
			// PRINT LABELS
			if (filter instanceof ILabeled) {
				ILabeled holder = (ILabeled) filter;
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

	@NoArgsConstructor
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

	protected static class Mapper implements Language.Mapper {

		private String qry;
		private DataMap<IFilter, String, FilterStatus> map;

		protected Mapper(String qry, DataMap<IFilter, String, FilterStatus> map) {
			this.qry = qry;
			this.map = map;
		}

		@Override
		public String qry() {
			return qry;
		}

		@Override
		public <ID extends Serializable> void updateObjectIds(PatternStorage storage, Map<String, ID> ids) {
			this.map.forEach((filter, code) -> {
				if (filter instanceof DataFilter) {
					Object data = ((DataFilter) filter).getData();
					Serializable id = ids.get("id_" + code);
					storage.getNodeBuffer().save(id, data);
					storage.setId(data, id);
				}
			});
		}

		@Override
		public Object parseObject(Map<String, Data> record) {
			IPatternContainer container = null;
			List<IPattern.Data> list = new ArrayList<>();

			for (IFilter filter : this.map.keySet()) {
				if (container == null) {
					if (!(filter instanceof IPatternContainer))
						return null;
					container = (IPatternContainer) filter;
				}

				list.add(new PData(record.get(this.map.get(filter)), filter));
			}

			try {
				return container.getPattern().parse(list);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public Collection<Object> parseObjects(List<Map<String, Data>> records) {
			Collection<Object> list = new ArrayList<>();
			for (Map<String, Data> record : records)
				list.add(this.parseObject(record));
			return list;
		}

		@Override
		public String toString() {
			return this.qry;
		}
	}

	@Getter
	protected static class PData implements IPattern.Data {
		private Serializable id;
		private Set<String> labels;
		private String data;
		private IFilter filter;

		protected PData(Module.Data data, IFilter filter) {
			this.id = data.getId();
			this.labels = data.getLabels();
			this.data = data.getData();
			this.filter = filter;
		}
	}
}
