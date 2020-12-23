package net.runeduniverse.libs.rogm.querying;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

public abstract class AParamFilter<F extends IFilter> extends AParamHolder<F> implements ILabeled {

	@Getter
	protected Set<String> labels = new HashSet<>();

	// LABEL
	public F addLabel(String label) {
		this.labels.add(label);
		return this.instance;
	}

	public F addLabels(Collection<String> labels) {
		this.labels.addAll(labels);
		return this.instance;
	}
}
