package net.runeduniverse.libs.rogm.querying;

import java.util.List;

public interface IFNode extends IFilter, ILabeled{
	List<IFilter> getRelations();
}
