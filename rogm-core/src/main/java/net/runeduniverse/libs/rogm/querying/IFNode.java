package net.runeduniverse.libs.rogm.querying;

import java.util.Set;

public interface IFNode extends IFilter, ILabeled {
	Set<IFRelation> getRelations();
}
