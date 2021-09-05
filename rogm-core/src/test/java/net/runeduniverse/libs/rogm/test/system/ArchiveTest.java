package net.runeduniverse.libs.rogm.test.system;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
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
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;

public class ArchiveTest extends AConfigTest {

	static {
		Archive.PACKAGE_SCANNER_DEBUG_MODE = true;
	}

	protected final Archive archive;
	protected final QueryBuilder qryBuilder;

	public ArchiveTest(Configuration cnf) {
		super(cnf);
		this.archive = new Archive(this.cnf.getPackageInfo(), this.cnf.getModule());
		this.qryBuilder = this.archive.getQueryBuilder();
	}

	public ArchiveTest() {
		this(new Configuration(null, null, new DummyModule(), null).addClassLoader(ArchiveTest.class.getClassLoader())
				.setLogger(new DebugLogger(Logger.getLogger(ArchiveTest.class.getName())))
				.addPackage(MODEL_PKG_PATH)
				.addPackage(RELATIONS_PKG_PATH));
	}

	@BeforeAll
	@Tag("system")
	public void scanModels() throws ScannerException {
		for (PassiveModule module : this.cnf.getPassiveModules())
			module.configure(archive);
	}

	public String printQuery(Class<?> clazz, IFilter filter) throws Exception {
		return "[QUERY][" + clazz.getSimpleName() + "]\n" + iLanguage.load(filter) + '\n';
	}

	public String printSave(Object entity, int depth) throws Exception {
		this.archive.logPatterns(new ConsoleLogger());
		return "[SAVE][" + entity.getClass()
				.getSimpleName() + "]\n"
				+ iLanguage.save(this.archive.save(entity.getClass(), entity, depth)
						.getDataContainer(), null)
						.qry()
				+ '\n';
	}

}
