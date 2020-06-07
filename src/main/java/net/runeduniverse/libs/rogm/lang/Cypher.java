package net.runeduniverse.libs.rogm.lang;

import java.util.HashMap;
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
		StringBuilder builder = new StringBuilder();
		Map<Filter, String> map = new HashMap<>();
		StringVariableGenerator gen = new StringVariableGenerator();

		parse(map, filter, gen);

		// MATCHES
		for (Filter f : map.keySet()) {
			if (f instanceof IdentifiedFilter) {
				String s = map.get(f);
				builder.append("MATCH (" + s + ")\nWHERE id(" + s + ")=" + ((IdentifiedFilter) f).getId() + "\n");
			} else if (f instanceof FNode) {
				builder.append("MATCH " + filterToString(map, f, true) + '\n');
			} else if (f instanceof FRelation) {
				FRelation rel = (FRelation) f;
				builder.append("MATCH ");
				StringBuilder matchLine = new StringBuilder(filterToString(map, rel.getStart(), true));

				switch (rel.getDirection()) {
				case INCOMING:
					matchLine.append("<-");
					break;
				case OUTGOING:
				case BIDIRECTIONAL:
					matchLine.append('-');
				}

				matchLine.append(filterToString(map, rel, false));

				switch (rel.getDirection()) {
				case OUTGOING:
					matchLine.append("->");
					break;
				case INCOMING:
				case BIDIRECTIONAL:
					matchLine.append('-');
				}

				matchLine.append(filterToString(map, rel.getTarget(), true));
				builder.append(matchLine.toString() + '\n');
			}
		}

		builder.append("RETURN " + map.get(filter) + ';');
		return builder.toString();
	}

	private void parse(Map<Filter, String> map, Filter filter, StringVariableGenerator gen) throws Exception {
		if (map.containsKey(filter))
			return;
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
			throw new Exception("Filter not supported");
	}

	private String filterToString(Map<Filter, String> map, Filter filter, boolean isNode) {
		StringBuilder builder = new StringBuilder(map.get(filter));

		if (filter instanceof ParamFilter) {
			ParamFilter param = (ParamFilter) filter;
			for (String label : param.getLabels())
				builder.append(':' + label.replace(' ', '_'));

			builder.append(" {");
			// add params
			builder.append('}');
		}

		if (isNode)
			return '(' + builder.toString() + ')';
		else
			return '[' + builder.toString() + ']';
	}

}
