package net.runeduniverse.libs.rogm.querying;

import java.util.Set;

public interface ILabeled {

	Set<String> getLabels();

	public default String getPrimaryLabel() {
		for (String label : this.getLabels())
			return label;
		return "";
	}
}
