package net.runeduniverse.libs.rogm.test.system;

import java.util.logging.Logger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.logging.DebugLogger;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.test.ATest;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;

public class ArchiveTest {
	public static final String MODEL_PKG_PATH = ATest.MODEL_PKG_PATH;
	public static final String RELATIONS_PKG_PATH = ATest.RELATIONS_PKG_PATH;
	
	static {
		Archive.PACKAGE_SCANNER_DEBUG_MODE = true;
	}
	
	protected final Configuration cnf;
	protected final Archive archive;
	
	public ArchiveTest() {
		this.cnf = new Configuration(null, null, new DummyModule(), null).addClassLoader(this.getClass().getClassLoader());
		cnf.setLogger(new DebugLogger(Logger.getLogger(ArchiveTest.class.getName())));

		cnf.addPackage(MODEL_PKG_PATH);
		cnf.addPackage(RELATIONS_PKG_PATH);
		
		this.archive = new Archive(this.cnf);
	}
	
	@Test
	@Tag("system")
	public void scanModels() throws ScannerException {
		this.archive.applyConfig();
	}
	
}
