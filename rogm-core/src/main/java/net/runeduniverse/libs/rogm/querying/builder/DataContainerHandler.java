package net.runeduniverse.libs.rogm.querying.builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IDataContainer;

@NoArgsConstructor
@Setter
public class DataContainerHandler implements IDataContainer, NoFilterType {

	@Getter
	private Object data = null;
	private boolean persist = false;
	@Getter
	private boolean readonly = false;

	@Override
	public boolean persist() {
		return this.persist;
	}

	public static boolean required(DataContainerHandler instance, final FilterType filterType) {
		if (filterType == FilterType.CREATE || filterType == FilterType.UPDATE)
			return true;
		if (instance == null)
			return false;
		return instance.data != null || instance.persist || instance.readonly;
	}
}
