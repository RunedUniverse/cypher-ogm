package net.runeduniverse.libs.rogm.entities.base;

import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.entities.ABaseScanner;
import net.runeduniverse.libs.rogm.scanner.ResultConsumer;

public class RelationScanner extends ABaseScanner {

	public RelationScanner(ResultConsumer consumer) {
		super(RelationshipEntity.class, consumer);
	}

	

}
