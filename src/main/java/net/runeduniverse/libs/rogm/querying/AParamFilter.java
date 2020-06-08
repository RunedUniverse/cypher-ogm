package net.runeduniverse.libs.rogm.querying;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

public abstract class AParamFilter<F extends Filter> extends FParamHolder implements ParamFilter{

	private F instance = null;
	protected void setInstance(F instance) {
		this.instance = instance;
	}
	
	@Getter
	protected Set<String> labels = new HashSet<>();
	
	// LABEL
	public F addLabel(String label) {
		this.labels.add(label);
		return this.instance;
	}
	public F addLabels(List<String> labels) {
		this.labels.addAll(labels);
		return this.instance;
	}
}
