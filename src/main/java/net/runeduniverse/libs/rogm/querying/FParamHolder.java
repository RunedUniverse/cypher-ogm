package net.runeduniverse.libs.rogm.querying;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class FParamHolder implements ParamHolder {

	@Getter
	private Map<String, Object> params = new HashMap<>();

	public FParamHolder addParam(String label, Object value) {
		this.params.put(label, value);
		return this;
	}
	
}
