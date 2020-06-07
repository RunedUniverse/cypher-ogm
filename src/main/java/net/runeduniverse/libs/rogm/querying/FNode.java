package net.runeduniverse.libs.rogm.querying;

import java.util.List;

public interface FNode extends Filter, ParamFilter{
	List<Filter> getRelations();
}
