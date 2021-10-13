package net.runeduniverse.libs.rogm.querying.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.logging.logs.CompoundTree;
import net.runeduniverse.libs.rogm.querying.IOptional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OptionalHandler implements IOptional, NoFilterType, ITraceable {

	private boolean optional = false;

	@Override
	public void toRecord(CompoundTree tree) {
		tree.append("OPTIONAL", this.optional ? "TRUE" : "FALSE");
	}
}
