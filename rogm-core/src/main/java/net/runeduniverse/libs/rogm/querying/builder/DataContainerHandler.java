package net.runeduniverse.libs.rogm.querying.builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.querying.IDataContainer;

@NoArgsConstructor
@Setter
public class DataContainerHandler implements IDataContainer, NoFilterType {

	@Getter
	private Object data;
	private boolean persist;
	@Getter
	private boolean readonly;

	@Override
	public boolean persist() {
		return this.persist;
	}
}
