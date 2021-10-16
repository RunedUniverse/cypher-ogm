package net.runeduniverse.libs.rogm.querying.builder;

import net.runeduniverse.libs.logging.logs.CompoundTree;

public interface ITraceable {

	void toRecord(CompoundTree tree);

	static void toRecord(CompoundTree tree, Object object) {
		if (object instanceof ITraceable)
			((ITraceable) object).toRecord(tree);
	}
}
