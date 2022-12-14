package net.runeduniverse.lib.rogm.api.pattern;

import java.io.Serializable;
import java.util.Set;

import net.runeduniverse.lib.rogm.api.buffer.LoadState;
import net.runeduniverse.lib.rogm.api.querying.IFilter;

public interface IData {
	Serializable getId();

	Serializable getEntityId();

	void setEntityId(Serializable entityId);

	Set<String> getLabels();

	String getData();

	IFilter getFilter();

	default LoadState getLoadState() {
		return LoadState.get(this.getFilter());
	}

	default String valuesToString() {
		return "IData[" + this.hashCode() + "]\nid:        " + this.getId() + "\nentity_id: " + this.getEntityId()
				+ "\nlables:    [" + String.join(", ", this.getLabels()) + "]\ndata:      " + this.getData()
				+ "\nfilter:    " + this.getFilter();
	}
}