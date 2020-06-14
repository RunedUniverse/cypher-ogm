package net.runeduniverse.libs.rogm.lang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.FNode;
import net.runeduniverse.libs.rogm.querying.FRelation;
import net.runeduniverse.libs.rogm.querying.Filter;
import net.runeduniverse.libs.rogm.querying.IdentifiedFilter;
import net.runeduniverse.libs.rogm.querying.ParamFilter;
import net.runeduniverse.libs.rogm.util.ModifiableHashMap;
import net.runeduniverse.libs.rogm.util.ModifiableMap;
import net.runeduniverse.libs.rogm.util.StringVariableGenerator;

public class Cypher implements Language {

	@Override
	public String buildQuery(Filter filter, Parser parser) throws Exception {
		ModifiableMap<Filter, String, FilterStatus> map = new ModifiableHashMap<>();
		StringBuilder qry = match(map, filter, parser);
		String key = map.get(filter);
		return qry.append("RETURN id(" + key + ") as id, " + key + ';').toString();
	}

	@Override
	public String buildInsert() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buildUpdate() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private StringBuilder match(ModifiableMap<Filter, String, FilterStatus> map, Filter filter, Parser parser)
			throws Exception {
		StringBuilder matchBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();
		StringVariableGenerator gen = new StringVariableGenerator();
		parse(map, filter, gen);

		// MATCHES
		map.forEach((f, code, modifier) -> {
			if (f instanceof IdentifiedFilter) {
				if (!modifier.equals(FilterStatus.EXTENSION_PRINTED)) {
					whereBuilder
							.append("WHERE id(" + code + ")=" + ((IdentifiedFilter<?>) f).getId().toString() + "\n");
					map.setModifier(f, FilterStatus.EXTENSION_PRINTED);

					if (!modifier.equals(FilterStatus.PRINTED)) {
						matchBuilder.append("MATCH (" + code + ")\n");
						map.setModifier(f, FilterStatus.PRINTED);
					}
				}

			} else if (f instanceof FNode) {
				if (!modifier.equals(FilterStatus.PRINTED)) {
					matchBuilder.append("MATCH " + filterToString(map, f, true, parser) + '\n');
				}

			} else if (f instanceof FRelation) {
				FRelation rel = (FRelation) f;
				matchBuilder.append("MATCH ");
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

				matchLine.append(filterToString(map, rel.getTarget(), true, parser));
				matchBuilder.append(matchLine.toString() + '\n');
			}
		});
		return matchBuilder.append(whereBuilder);
	}

	private void parse(ModifiableMap<Filter, String, FilterStatus> map, Filter filter, StringVariableGenerator gen)
			throws Exception {
		if (map.containsKey(filter))
			return;
		if (filter == null) {
			map.put(filter, "", FilterStatus.INITIALIZED);
			return;
		}
		map.put(filter, gen.nextVal(), FilterStatus.INITIALIZED);

		if (filter instanceof IdentifiedFilter) {
			IdentifiedFilter<?> idf = (IdentifiedFilter<?>) filter;
			if (!(idf.checkType(Long.class) || idf.checkType(Integer.class) || idf.checkType(Short.class)))
				throw new Exception("Filter ID <" + idf.getId().getClass().toString() + "> not supported");
		} else if (filter instanceof FNode) {
			for (Filter f : ((FNode) filter).getRelations())
				parse(map, f, gen);
		} else if (filter instanceof FRelation) {
			parse(map, ((FRelation) filter).getStart(), gen);
			parse(map, ((FRelation) filter).getTarget(), gen);
		} else
			throw new Exception("Filter<" + filter.toString() + "> not supported");
	}

	private String filterToString(ModifiableMap<Filter, String, FilterStatus> map, Filter filter, boolean isNode,
			Parser parser) {
		StringBuilder builder = new StringBuilder(map.get(filter));

		if (!map.getModifier(filter).equals(FilterStatus.PRINTED) && filter instanceof ParamFilter) {
			ParamFilter param = (ParamFilter) filter;
			for (String label : param.getLabels())
				builder.append(':' + label.replace(' ', '_'));

			if (!param.getParams().isEmpty())
				try {
					builder.append(' ' + parser.serialize(param));
				} catch (Exception e) {
					e.printStackTrace();
				}

			// disable future param parsing
			map.setModifier(filter, FilterStatus.PRINTED);
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
}
