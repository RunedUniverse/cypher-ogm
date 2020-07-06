package net.runeduniverse.libs.rogm.querying;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public abstract class AParamHolder<F extends IFilter> extends AFilter<F> implements IParameterized {

	@Getter
	private Map<String, Object> params = new HashMap<>();

	public F addParam(String label, Object value) {
		this.params.put(label, value);
		return this.instance;
	}
	
}
