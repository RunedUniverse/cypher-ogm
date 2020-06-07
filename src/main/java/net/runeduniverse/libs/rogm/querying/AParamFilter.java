package net.runeduniverse.libs.rogm.querying;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public abstract class AParamFilter<F extends Filter> implements ParamFilter{

	private F instance = null;
	protected void setInstance(F instance) {
		this.instance = instance;
	}
	
	@Getter
	protected List<String> labels = new ArrayList<>();
	
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
