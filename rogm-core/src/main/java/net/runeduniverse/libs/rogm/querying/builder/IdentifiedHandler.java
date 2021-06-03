package net.runeduniverse.libs.rogm.querying.builder;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IIdentified;

@NoArgsConstructor
public class IdentifiedHandler implements IIdentified<Serializable> {
	@Getter
	@Setter
	private Serializable id;

	@Override
	public FilterType getFilterType() {
		// not required here!
		return null;
	}

}
