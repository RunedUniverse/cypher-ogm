package net.runeduniverse.libs.rogm.querying.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.querying.IDataContainer;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DataContainerHandler implements IDataContainer, NoFilterType {

	private Object data;
	private boolean persist;
	private boolean readonly;

	@Override
	public boolean persist() {
		return this.persist;
	}
}
