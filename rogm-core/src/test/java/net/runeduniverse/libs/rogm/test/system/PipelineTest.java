package net.runeduniverse.libs.rogm.test.system;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.logging.Logger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.Session;
import net.runeduniverse.libs.logging.DebugLogger;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pipeline.DatabasePipelineFactory;
import net.runeduniverse.libs.rogm.pipeline.Pipeline;
import net.runeduniverse.libs.rogm.test.AConfigTest;
import net.runeduniverse.libs.rogm.test.dummies.DummyLanguage;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;
import net.runeduniverse.libs.rogm.test.dummies.DummyParser;

public class PipelineTest {
	public static final String MODEL_PKG_PATH = AConfigTest.MODEL_PKG_PATH;
	public static final String RELATIONS_PKG_PATH = AConfigTest.RELATIONS_PKG_PATH;

	static {
		Archive.PACKAGE_SCANNER_DEBUG_MODE = true;
	}

	protected final Configuration cnf;
	protected final DatabasePipelineFactory databasePipelineFactory;

	public PipelineTest() {
		this.cnf = new Configuration(new DummyParser(), new DummyLanguage(), new DummyModule(), null)
				.addClassLoader(this.getClass()
						.getClassLoader());
		cnf.setLogger(new DebugLogger(Logger.getLogger(PipelineTest.class.getName())));

		cnf.addPackage(MODEL_PKG_PATH);
		cnf.addPackage(RELATIONS_PKG_PATH);

		this.databasePipelineFactory = new DatabasePipelineFactory(this.cnf);
	}

	@Test
	@Tag("system")
	public void setupDatabasePipeline() throws Exception {
		@SuppressWarnings("resource")
		Pipeline pipeline = new Pipeline(this.databasePipelineFactory);
		Session session = pipeline.buildSession();
		assertNotNull(session, "Database Session wasn't built!");
	}
}
