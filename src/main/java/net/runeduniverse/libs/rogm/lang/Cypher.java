package net.runeduniverse.libs.rogm.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.FNode;
import net.runeduniverse.libs.rogm.querying.FRelation;
import net.runeduniverse.libs.rogm.querying.Filter;
import net.runeduniverse.libs.rogm.querying.IdentifiedFilter;
import net.runeduniverse.libs.rogm.querying.ParamFilter;
import net.runeduniverse.libs.rogm.util.StringVariableGenerator;

public class Cypher implements Language {

	@Override
	public String buildQuery(Filter filter, Parser parser) throws Exception {
		Map<Filter, String> map = new HashMap<>();
		StringBuilder qry = match(map, filter, parser);
		String key = map.get(filter);
		return qry.append("RETURN id(" + key + ") as id, " + key + ';').toString();
	}

	private StringBuilder match(Map<Filter, String> map, Filter filter, Parser parser) throws Exception {
		StringBuilder matchBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();
		StringVariableGenerator gen = new StringVariableGenerator();
		List<Filter> cFilter = new ArrayList<>();
		List<Filter> idcFilter = new ArrayList<>();

		parse(map, filter, gen);

		// MATCHES
		for (Filter f : map.keySet()) {
			if (f instanceof IdentifiedFilter) {
				String s = map.get(f);
				if (!cFilter.contains(filter)) {
					matchBuilder.append("MATCH (" + s + ")\n");
					cFilter.add(f);
				}
				if (!idcFilter.contains(filter)) {
					whereBuilder.append("WHERE id(" + s + ")=" + ((IdentifiedFilter<?>) f).getId().toString() + "\n");
					idcFilter.add(f);
				}

			} else if (f instanceof FNode) {
				if (!cFilter.contains(filter)) {
					matchBuilder.append("MATCH " + filterToString(map, f, true, cFilter, parser) + '\n');
					cFilter.add(f);
				}

			} else if (f instanceof FRelation) {
				FRelation rel = (FRelation) f;
				matchBuilder.append("MATCH ");
				StringBuilder matchLine = new StringBuilder(filterToString(map, rel.getStart(), true, cFilter, parser));

				switch (rel.getDirection()) {
				case INCOMING:
					matchLine.append("<-");
					break;
				case OUTGOING:
				case BIDIRECTIONAL:
					matchLine.append('-');
				}

				matchLine.append(filterToString(map, rel, false, cFilter, parser));

				switch (rel.getDirection()) {
				case OUTGOING:
					matchLine.append("->");
					break;
				case INCOMING:
				case BIDIRECTIONAL:
					matchLine.append('-');
				}

				matchLine.append(filterToString(map, rel.getTarget(), true, cFilter, parser));
				matchBuilder.append(matchLine.toString() + '\n');
			}
		}
		return matchBuilder.append(whereBuilder);
	}

	private void parse(Map<Filter, String> map, Filter filter, StringVariableGenerator gen) throws Exception {
		if (map.containsKey(filter))
			return;
		if (filter == null) {
			map.put(filter, "");
			return;
		}
		map.put(filter, gen.nextVal());

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

	private String filterToString(Map<Filter, String> map, Filter filter, boolean isNode, List<Filter> cFilter,
			Parser parser) {
		StringBuilder builder = new StringBuilder(map.get(filter));

		if (!cFilter.contains(filter) && filter instanceof ParamFilter) {
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
			cFilter.add(filter);
		}

		if (isNode)
			return '(' + builder.toString() + ')';
		else
			return '[' + builder.toString() + ']';
	}

}
