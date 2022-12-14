package net.runeduniverse.lib.rogm.api.container;

import net.runeduniverse.lib.rogm.api.pattern.IBaseQueryPattern;
import net.runeduniverse.lib.rogm.api.querying.IFilter;

public interface IPatternContainer extends IFilter {
	IBaseQueryPattern<?> getPattern();

	public static boolean identify(IFilter filter) {
		return filter instanceof IPatternContainer && ((IPatternContainer) filter).getPattern() != null;
	}
}