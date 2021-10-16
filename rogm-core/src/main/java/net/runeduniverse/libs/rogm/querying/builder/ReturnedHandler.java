package net.runeduniverse.libs.rogm.querying.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.logging.logs.CompoundTree;
import net.runeduniverse.libs.rogm.querying.IReturned;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReturnedHandler implements IReturned, NoFilterType, ITraceable {

	private boolean returned = false;

	@Override
	public void toRecord(CompoundTree tree) {
		tree.append("RETURNED", this.returned ? "TRUE" : "FALSE");
	}
}
