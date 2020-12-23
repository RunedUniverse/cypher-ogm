package net.runeduniverse.libs.rogm.querying;

import lombok.Getter;

public abstract class AFilter<F extends IFilter> implements IReturned, IOptional {

	protected F instance = null;
	@Getter
	private boolean returned = false;
	@Getter
	private boolean optional = false;
	@Getter
	private FilterType filterType = FilterType.MATCH;

	protected void setInstance(F instance) {
		this.instance = instance;
	}

	public F setReturned(boolean returning) {
		this.returned = returning;
		return this.instance;
	}

	public F setOptional(boolean optional) {
		this.optional = optional;
		return this.instance;
	}
}
