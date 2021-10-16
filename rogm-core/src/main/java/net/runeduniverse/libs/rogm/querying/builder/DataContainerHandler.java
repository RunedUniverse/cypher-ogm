package net.runeduniverse.libs.rogm.querying.builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.logging.logs.CompoundTree;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IDataContainer;

@NoArgsConstructor
@Setter
public class DataContainerHandler implements IDataContainer, NoFilterType, ITraceable {

	@Getter
	private Object data = null;
	private boolean persist = false;
	@Getter
	private boolean readonly = false;

	@Override
	public boolean persist() {
		return this.persist;
	}

	@Override
	public void toRecord(CompoundTree tree) {
		tree.append("DATA", data == null ? "null" : data.toString())
				.append("PERSIST", this.persist ? "TRUE" : "FALSE")
				.append("READONLY", this.readonly ? "TRUE" : "FALSE");
	}

	public static boolean required(DataContainerHandler instance, final FilterType filterType) {
		if (filterType == FilterType.CREATE || filterType == FilterType.UPDATE)
			return true;
		if (instance == null)
			return false;
		return instance.data != null || instance.persist || instance.readonly;
	}
}
