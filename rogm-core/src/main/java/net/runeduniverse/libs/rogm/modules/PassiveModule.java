package net.runeduniverse.libs.rogm.modules;

import net.runeduniverse.libs.rogm.errors.ScannerException;
import net.runeduniverse.libs.rogm.pattern.Archive;

public interface PassiveModule {

	default void configure(Archive archive) throws ScannerException {

	}

}
