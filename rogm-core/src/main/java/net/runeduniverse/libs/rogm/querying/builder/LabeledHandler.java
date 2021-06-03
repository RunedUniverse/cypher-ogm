package net.runeduniverse.libs.rogm.querying.builder;

import java.util.Collection;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.runeduniverse.libs.rogm.querying.ILabeled;

@NoArgsConstructor
public class LabeledHandler implements ILabeled {

	@Getter
	private Set<String> labels;

	public LabeledHandler addLabel(String label) {
		this.labels.add(label);
		return this;
	}

	public LabeledHandler addLabels(Collection<String> labels) {
		this.labels.addAll(labels);
		return this;
	}
}
