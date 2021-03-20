package net.runeduniverse.libs.rogm.modules;

import java.util.List;

import net.runeduniverse.libs.rogm.entities.APatternScanner;

public interface PassiveModule {

	default List<APatternScanner> getPatternScanner() {
		return null;
	}

}
