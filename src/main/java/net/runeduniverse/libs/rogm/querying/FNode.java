package net.runeduniverse.libs.rogm.querying;

import java.util.List;

public interface FNode extends Filter, LabelHolder{
	List<Filter> getRelations();
}
