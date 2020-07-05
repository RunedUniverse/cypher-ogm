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
	public String buildQuery(Filter filter, Parser parser) throws Exception {
		DataMap<Filter, String, FilterStatus> map = new DataHashMap<>();
		StringVariableGenerator gen = new StringVariableGenerator();
		parse(map, filter, gen);

		StringBuilder qry = select(map, parser, Phase.MATCH).append("RETURN");
		List<String> rt = new ArrayList<>();

		map.forEach((f, c) -> {
			if (f instanceof ReturnHolder && ((ReturnHolder) f).isReturned())
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
		DataMap<Filter, String, FilterStatus> map = new DataHashMap<>();
		StringVariableGenerator gen = new StringVariableGenerator();
		parse(map, node, gen);

		StringBuilder qry = select(map, parser, Phase.CREATE);

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
		DataMap<Filter, String, FilterStatus> map = new DataHashMap<>();
		StringVariableGenerator gen = new StringVariableGenerator();
		parse(map, node, gen);

		StringBuilder qry = select(map, parser, Phase.MATCH);

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

	private void parse(DataMap<Filter, String, FilterStatus> map, Filter filter, StringVariableGenerator gen)
			throws Exception {
		if (map.containsKey(filter))
			return;
		if (filter == null) {
			map.put(filter, "", FilterStatus.INITIALIZED);
			return;
		}
		map.put(filter, gen.nextVal(), FilterStatus.INITIALIZED);

		// vv START - to be remodeled
		if (filter instanceof IdentifiedFilter) {
			IdentifiedFilter<?> idf = (IdentifiedFilter<?>) filter;
			if (!(idf.checkType(Long.class) || idf.checkType(Integer.class) || idf.checkType(Short.class)))
				throw new Exception("Filter ID <" + idf.getId().getClass().toString() + "> not supported");
		} else
		// ^^ END
		if (filter instanceof FNode) {
			for (Filter f : ((FNode) filter).getRelations())
				parse(map, f, gen);
		} else if (filter instanceof FRelation) {
			parse(map, ((FRelation) filter).getStart(), gen);
			parse(map, ((FRelation) filter).getTarget(), gen);
		} else
			throw new Exception("Filter<" + filter.toString() + "> not supported");
	}

	private StringBuilder select(DataMap<Filter, String, FilterStatus> map, Parser parser, Phase phase)
			throws Exception {
		StringBuilder matchBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();

		// MATCHES
		map.forEach((f, code, modifier) -> {
			if (f == null || modifier.equals(FilterStatus.PRINTED))
				return;

			matchBuilder.append(phase.prefix);
			// vv START - to be remodeled
			if (phase != Phase.CREATE && f instanceof IdentifiedFilter) {
				if (!modifier.equals(FilterStatus.EXTENSION_PRINTED)) {
					whereBuilder
							.append("WHERE id(" + code + ")=" + ((IdentifiedFilter<?>) f).getId().toString() + "\n");
					map.setData(f, FilterStatus.EXTENSION_PRINTED);

					if (!modifier.equals(FilterStatus.PRINTED)) {
						matchBuilder.append('(' + code + ')');
						map.setData(f, FilterStatus.PRINTED);
					}
				}

			} else
			// ^^ END
			if (f instanceof FNode) {
				if (!modifier.equals(FilterStatus.PRINTED))
					matchBuilder.append(filterToString(map, f, true, parser));

			} else if (f instanceof FRelation)
				matchBuilder.append(translateRelation(map, parser, (FRelation) f).toString());
			else
				matchBuilder.append("()");
			matchBuilder.append('\n');
		});
		return matchBuilder.append(whereBuilder);
	}

	private StringBuilder translateRelation(DataMap<Filter, String, FilterStatus> map, Parser parser, FRelation rel) {
		StringBuilder matchLine = new StringBuilder(filterToString(map, rel.getStart(), true, parser));

		switch (rel.getDirection()) {
		case INCOMING:
			matchLine.append("<-");
			break;
		case OUTGOING:
		case BIDIRECTIONAL:
			matchLine.append('-');
		}

		matchLine.append(filterToString(map, rel, false, parser));

		switch (rel.getDirection()) {
		case OUTGOING:
			matchLine.append("->");
			break;
		case INCOMING:
		case BIDIRECTIONAL:
			matchLine.append('-');
		}

		return matchLine.append(filterToString(map, rel.getTarget(), true, parser));
	}

	private String filterToString(DataMap<Filter, String, FilterStatus> map, Filter filter, boolean isNode,
			Parser parser) {
		StringBuilder builder = new StringBuilder(map.get(filter));

		if (!map.getData(filter).equals(FilterStatus.PRINTED)) {
			// PRINT LABELS
			if (filter instanceof LabelHolder) {
				LabelHolder holder = (LabelHolder) filter;
				for (String label : holder.getLabels())
					builder.append(':' + label.replace(' ', '_'));
			}
			// PRINT DATA
			if (filter instanceof ParamHolder) {
				ParamHolder holder = (ParamHolder) filter;
				if (!holder.getParams().isEmpty())
					try {
						builder.append(' ' + parser.serialize(holder.getParams()));
					} catch (Exception e) {
						e.printStackTrace();
					}
			} else if (filter instanceof DataHolder) {
				DataHolder holder = (DataHolder) filter;
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
		private DataMap<Filter, String, FilterStatus> map;

		protected Mapper(String qry, DataMap<Filter, String, FilterStatus> map) {
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
