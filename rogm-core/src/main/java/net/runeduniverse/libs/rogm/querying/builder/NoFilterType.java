package net.runeduniverse.libs.rogm.querying.builder;

import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface NoFilterType extends IFilter {
	@Override
	default FilterType getFilterType() {
		// not required here!
		return null;
	}
}
