package net.runeduniverse.libs.rogm.entities.base;

import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.entities.ABaseScanner;
import net.runeduniverse.libs.rogm.scanner.ResultConsumer;

public class NodeScanner extends ABaseScanner {

	public NodeScanner(ResultConsumer consumer) {
		super(NodeEntity.class, consumer);
	}

}
