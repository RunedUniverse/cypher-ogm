package net.runeduniverse.libs.rogm.modules;

import java.util.List;

import net.runeduniverse.libs.rogm.entities.PatternScanner;

public interface PassiveModule {

	default List<PatternScanner> getPatternScanner() {
		return null;
	}

}
