package net.runeduniverse.libs.rogm.modules;

import java.util.List;

import net.runeduniverse.libs.rogm.entities.scanner.TypeScanner;

public interface PassiveModule {

	default List<TypeScanner> getPatternScanner() {
		return null;
	}

}
