package net.runeduniverse.libs.rogm.modules;

import java.util.List;

import net.runeduniverse.libs.rogm.entities.ABaseScanner;

public interface PassiveModule {

	default List<ABaseScanner> getPatternScanner() {
		return null;
	}

}
