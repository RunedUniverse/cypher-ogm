package net.runeduniverse.libs.rogm.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.*;
import net.runeduniverse.libs.rogm.util.*;

public class Cypher implements Language {

	@Override
	public String buildQuery(IFilter filter, Parser parser) throws Exception {
		DataMap<IFilter, String, FilterStatus> map = new DataHashMap<>();
		StringVariableGenerator gen = new StringVariableGenerator();
		_parse(map, filter, gen);

		StringBuilder qry = _select(map, parser, Phase.MATCH).append("RETURN");
		List<String> rt = new ArrayList<>();

		map.forEach((f, c) -> {
			if (f instanceof IReturned && ((IReturned) f).isReturned())
				rt.add("id(" + c + ") as id_" + c + ", " + c);
		});

		if (rt.isEmpty()) {
			String c = map.get(filter);
			qry.append(" id(" + c + ") as id_" + c + ", " + c);
		} else
			qry.append(String.join(", ", rt));

		return qry.append(';').toString();
	}

	@Override
	public Mapper buildInsert(DataFilter node, Parser parser) throws Exception {
		DataMap<IFilter, String, FilterStatus> map = new DataHashMap<>();
		StringVariableGenerator gen = new StringVariableGenerator();
		_parse(map, node, gen);

		StringBuilder qry = _select(map, parser, Phase.CREATE);

		List<String> st = new ArrayList<>();
		List<String> rt = new ArrayList<>();

		map.forEach((f, c) -> {
			try {
				DataFilter d = (DataFilter) f;
				st.add(c + '=' + parser.serialize(d.getData()));
				rt.add("id(" + c + ") as id_" + c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// SET + RETURN
		return new Mapper(qry.append("SET ").append(String.join(", ", st)).append("\nRETURN ")
				.append(String.join(", ", rt)).append(';').toString(), map);
	}

	@Override
	public Mapper buildUpdate(DataFilter node, Parser parser) throws Exception {
		DataMap<IFilter, String, FilterStatus> map = new DataHashMap<>();
		StringVariableGenerator gen = new StringVariableGenerator();
		_parse(map, node, gen);

		StringBuilder qry = _select(map, parser, Phase.MATCH);

		List<String> st = new ArrayList<>();
		List<String> rt = new ArrayList<>();

		map.forEach((f, c) -> {
			try {
				DataFilter d = (DataFilter) f;
				st.add(c + '=' + parser.serialize(d.getData()));
				rt.add("id(" + c + ") as id_" + c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// SET + RETURN
		return new Mapper(qry.append("SET ").append(String.join(", ", st)).append("\nRETURN ")
				.append(String.join(", ", rt)).append(';').toString(), map);
	}

	private void _parse(DataMap<IFilter, String, FilterStatus> map, IFilter filter, StringVariableGenerator gen)
			throws Exception {
		if (map.containsKey(filter))
			return;
		if (filter == null) {
			map.put(filter, "", FilterStatus.INITIALIZED);
			return;
		}
		map.put(filter, gen.nextVal(), FilterStatus.INITIALIZED);

		if (filter instanceof IFNode) {
			IIdentified.checkType(Number.class, filter);
			for (IFilter f : ((IFNode) filter).getRelations())
				_parse(map, f, gen);
		} else if (filter instanceof IFRelation) {
			IIdentified.checkType(Number.class, filter);
			_parse(map, ((IFRelation) filter).getStart(), gen);
			_parse(map, ((IFRelation) filter).getTarget(), gen);
		} else
			throw new Exception("IFilter<" + filter.toString() + "> not supported");
	}

	private StringBuilder _select(DataMap<IFilter, String, FilterStatus> map, Parser parser, Phase phase)
			throws Exception {
		StringBuilder matchBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();

		// MATCHES
		map.forEach((f, code, modifier) -> {
			if (f == null || modifier.equals(FilterStatus.PRINTED))
				return;

			matchBuilder.append(phase.prefix);
			if (f instanceof IFNode) {
				if (phase != Phase.CREATE && f instanceof IIdentified
						&& !modifier.equals(FilterStatus.EXTENSION_PRINTED))
					_where(map, whereBuilder, f, code);
				if (!modifier.equals(FilterStatus.PRINTED))
					matchBuilder.append(_filterToString(map, f, true, parser));
			} else if (f instanceof IFRelation) {
				if (phase != Phase.CREATE && f instanceof IIdentified
						&& !modifier.equals(FilterStatus.EXTENSION_PRINTED))
					_where(map, whereBuilder, f, code);
				matchBuilder.append(_translateRelation(map, parser, (IFRelation) f).toString());
			} else
				matchBuilder.append("()");
			matchBuilder.append('\n');
		});
		return matchBuilder.append(whereBuilder);
	}

	private void _where(DataMap<IFilter, String, FilterStatus> map, StringBuilder builder, IFilter f, String code) {
		IIdentified<?> i = (IIdentified<?>) f;
		builder.append("WHERE id(" + code + ")=" + ((Number) i.getId()).longValue() + "\n");
		map.setData(f, FilterStatus.EXTENSION_PRINTED);
	}

	private StringBuilder _translateRelation(DataMap<IFilter, String, FilterStatus> map, Parser parser, IFRelation rel) {
		StringBuilder matchLine = new StringBuilder(_filterToString(map, rel.getStart(), true, parser));

		switch (rel.getDirection()) {
		case INCOMING:
			matchLine.append("<-");
			break;
		case OUTGOING:
		case BIDIRECTIONAL:
			matchLine.append('-');
		}

		matchLine.append(_filterToString(map, rel, false, parser));

		switch (rel.getDirection()) {
		case OUTGOING:
			matchLine.append("->");
			break;
		case INCOMING:
		case BIDIRECTIONAL:
			matchLine.append('-');
		}

		return matchLine.append(_filterToString(map, rel.getTarget(), true, parser));
	}

	private String _filterToString(DataMap<IFilter, String, FilterStatus> map, IFilter filter, boolean isNode,
			Parser parser) {
		StringBuilder builder = new StringBuilder(map.get(filter));

		if (!map.getData(filter).equals(FilterStatus.PRINTED)) {
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
		public static final FilterStatus PRINTED = new FilterStatus(2);
		public static final FilterStatus EXTENSION_PRINTED = new FilterStatus(3);

		@Getter
		private int status = 0;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FilterStatus)
				return this.status >= ((FilterStatus) obj).getStatus();
			return super.equals(obj);
		}
	}

	@AllArgsConstructor
	protected static enum Phase {
		MATCH("MATCH "), CREATE("CREATE "), MERGE("MERGE ");

		String prefix;
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
		public <ID extends Serializable> void updateObjectIds(FieldAccessor accessor, Buffer nodeBuffer,
				Map<String, ID> ids) {
			this.map.forEach((filter, code) -> {
				if (filter instanceof DataFilter) {
					Object data = ((DataFilter) filter).getData();
					Serializable id = ids.get("id_" + code);
					nodeBuffer.save(id, data);
					accessor.setObjectId(data, id);
				}
			});
		}

	}
}
