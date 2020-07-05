package net.runeduniverse.libs.rogm.querying;

import lombok.Getter;

public abstract class AFilter<F extends Filter> implements ReturnHolder {
	
	protected F instance = null;
	@Getter
	private boolean returned = false;
	
	protected void setInstance(F instance) {
		this.instance = instance;
	}
	
	public F setReturning(boolean returning) {
		this.returned = returning;
		return this.instance;
	}
}
