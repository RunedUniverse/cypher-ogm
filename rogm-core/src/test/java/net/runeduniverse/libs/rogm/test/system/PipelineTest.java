package net.runeduniverse.libs.rogm.test.system;

import java.util.logging.Logger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.Session;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.logging.DebugLogger;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pipeline.DatabasePipelineFactory;
import net.runeduniverse.libs.rogm.pipeline.Pipeline;
import net.runeduniverse.libs.rogm.test.ATest;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;

public class PipelineTest {
	public static final String MODEL_PKG_PATH = ATest.MODEL_PKG_PATH;
	public static final String RELATIONS_PKG_PATH = ATest.RELATIONS_PKG_PATH;

	static {
		Archive.PACKAGE_SCANNER_DEBUG_MODE = true;
	}

	protected final Configuration cnf;
	protected final DatabasePipelineFactory databaseTransactionBuilder;

	public PipelineTest() {
		this.cnf = new Configuration(null, null, new DummyModule(), null).addClassLoader(this.getClass()
				.getClassLoader());
		cnf.setLogger(new DebugLogger(Logger.getLogger(PipelineTest.class.getName())));

		cnf.addPackage(MODEL_PKG_PATH);
		cnf.addPackage(RELATIONS_PKG_PATH);

		this.databaseTransactionBuilder = new DatabasePipelineFactory(this.cnf);
	}

	@Test
	@Tag("system")
	public void setupDatabasePipeline() throws ScannerException {
		Pipeline pipeline = new Pipeline(this.databaseTransactionBuilder);

		Session session = pipeline.buildSession();

	}
}
