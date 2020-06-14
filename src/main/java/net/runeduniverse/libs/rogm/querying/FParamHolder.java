package net.runeduniverse.libs.rogm.querying;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class FParamHolder<F extends Filter> extends AFilter<F> implements ParamHolder {

	@Getter
	private Map<String, Object> params = new HashMap<>();

	public FParamHolder<F> addParam(String label, Object value) {
		this.params.put(label, value);
		return this;
	}
	
}
