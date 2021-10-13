package net.runeduniverse.libs.rogm.test.system;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.logging.DebugLogger;
import net.runeduniverse.libs.rogm.modules.PassiveModule;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.rogm.test.AConfigTest;
import net.runeduniverse.libs.rogm.test.ConsoleLogger;
import net.runeduniverse.libs.rogm.test.dummies.DummyLanguage;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;
import net.runeduniverse.libs.rogm.test.dummies.DummyParser;

public class ArchiveTest extends AConfigTest {

	protected final Archive archive;
	protected final QueryBuilder qryBuilder;

	public ArchiveTest(Configuration cnf) {
		super(cnf);
		this.archive = new Archive(this.cnf.getPackageInfo(), this.cnf.getModule());
		this.qryBuilder = this.archive.getQueryBuilder();
	}

	public ArchiveTest() {
		this(new Configuration(new DummyParser(), new DummyLanguage(), new DummyModule(), "localhost")
				.addClassLoader(ArchiveTest.class.getClassLoader())
				.setLogger(new DebugLogger(Logger.getLogger(ArchiveTest.class.getName())))
				.addPackage(MODEL_PKG_PATH)
				.addPackage(RELATIONS_PKG_PATH));
	}

	@BeforeEach
	@Tag("system")
	public void scanModels() throws ScannerException {
		for (PassiveModule module : this.cnf.getPassiveModules())
			module.configure(this.archive);
	}

	protected String printQuery(Class<?> clazz, IFilter filter) throws Exception {
		return "[QUERY][" + clazz.getSimpleName() + "]\n" + iLanguage.load(filter) + '\n';
	}

	protected String printSave(Object entity, int depth) throws Exception {
		this.archive.logPatterns(new ConsoleLogger());
		return "[SAVE][" + entity.getClass()
				.getSimpleName() + "]\n"
				+ iLanguage.save(this.archive.save(entity.getClass(), entity, depth)
						.getDataContainer(), null)
						.qry()
				+ '\n';
	}

}
