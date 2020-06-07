package net.runeduniverse.libs.rogm.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runeduniverse.libs.rogm.querying.FNode;
import net.runeduniverse.libs.rogm.querying.FRelation;
import net.runeduniverse.libs.rogm.querying.Filter;
import net.runeduniverse.libs.rogm.querying.IdentifiedFilter;
import net.runeduniverse.libs.rogm.querying.ParamFilter;
import net.runeduniverse.libs.rogm.util.StringVariableGenerator;

public class Cypher implements Language {

	@Override
	public String buildQuery(Filter filter) throws Exception {
		Map<Filter, String> map = new HashMap<>();
		return match(map, filter).append("RETURN " + map.get(filter) + ';').toString();
	}

	private StringBuilder match(Map<Filter, String> map, Filter filter) throws Exception {
		StringBuilder matchBuilder = new StringBuilder();
		StringVariableGenerator gen = new StringVariableGenerator();
		List<Filter> cFilter = new ArrayList<>();
		List<Filter> idcFilter = new ArrayList<>();

		parse(map, filter, gen);

		// MATCHES
		for (Filter f : map.keySet()) {
			if (f instanceof IdentifiedFilter) {
				String s = map.get(f);
				if (!idcFilter.contains(filter)) {
					matchBuilder
							.append("MATCH (" + s + ")\nWHERE id(" + s + ")=" + ((IdentifiedFilter) f).getId() + "\n");
					idcFilter.add(f);
				}

			} else if (f instanceof FNode) {
				if (!cFilter.contains(filter)) {
					matchBuilder.append("MATCH " + filterToString(map, f, true, cFilter) + '\n');
					cFilter.add(f);
				}

			} else if (f instanceof FRelation) {
				FRelation rel = (FRelation) f;
				matchBuilder.append("MATCH ");
				StringBuilder matchLine = new StringBuilder(filterToString(map, rel.getStart(), true, cFilter));

				switch (rel.getDirection()) {
				case INCOMING:
					matchLine.append("<-");
					break;
				case OUTGOING:
				case BIDIRECTIONAL:
					matchLine.append('-');
				}

				matchLine.append(filterToString(map, rel, false, cFilter));

				switch (rel.getDirection()) {
				case OUTGOING:
					matchLine.append("->");
					break;
				case INCOMING:
				case BIDIRECTIONAL:
					matchLine.append('-');
				}

				matchLine.append(filterToString(map, rel.getTarget(), true, cFilter));
				matchBuilder.append(matchLine.toString() + '\n');
			}
		}
		return matchBuilder;
	}

	private void parse(Map<Filter, String> map, Filter filter, StringVariableGenerator gen) throws Exception {
		if (map.containsKey(filter))
			return;
		if(filter==null) {
			map.put(filter, "");
			return;
		}
		map.put(filter, gen.nextVal());

		if (filter instanceof IdentifiedFilter) {
			// IdentifiedFilter has no relations
		} else if (filter instanceof FNode) {
			for (Filter f : ((FNode) filter).getRelations())
				parse(map, f, gen);
		} else if (filter instanceof FRelation) {
			parse(map, ((FRelation) filter).getStart(), gen);
			parse(map, ((FRelation) filter).getTarget(), gen);
		} else
			throw new Exception("Filter<" + filter.toString() + "> not supported");
	}

	private String filterToString(Map<Filter, String> map, Filter filter, boolean isNode, List<Filter> cFilter) {
		StringBuilder builder = new StringBuilder(map.get(filter));

		if (!cFilter.contains(filter) && filter instanceof ParamFilter) {
			ParamFilter param = (ParamFilter) filter;
			for (String label : param.getLabels())
				builder.append(':' + label.replace(' ', '_'));

			builder.append(" {");
			// add params
			builder.append('}');

			// disable future param parsing
			cFilter.add(filter);
		}

		if (isNode)
			return '(' + builder.toString() + ')';
		else
			return '[' + builder.toString() + ']';
	}

}
