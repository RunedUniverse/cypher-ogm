package net.runeduniverse.libs.rogm.modules;

import net.runeduniverse.libs.rogm.errors.ScannerException;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.scanner.TypeScanner;

public abstract class AModule implements Module {

	@Override
	public void configure(Archive archive) throws ScannerException {
		archive.scan(new TypeScanner.NodeScanner(archive, p -> archive.addEntry(p.getType(), p)),
				new TypeScanner.RelationScanner(archive, p -> archive.addEntry(p.getType(), p)));
	}
}
