package net.runeduniverse.libs.rogm.test;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.errors.ScannerException;
import net.runeduniverse.libs.rogm.modules.PassiveModule;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.rogm.test.dummies.DummyLanguage;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;
import net.runeduniverse.libs.rogm.test.dummies.DummyParser;

public abstract class AArchiveTest extends AConfigTest {

	protected final Archive archive;
	protected final QueryBuilder qryBuilder;

	public AArchiveTest(Configuration cnf) {
		super(cnf);
		this.archive = new Archive(this.cnf.getPackageInfo(), this.cnf.getModule());
		this.qryBuilder = this.archive.getQueryBuilder();
	}

	public AArchiveTest(Configuration cnf, Logger logger) {
		this(cnf.setLogger(logger)
				.addClassLoader(AArchiveTest.class.getClassLoader())
				.addPackage(MODEL_PKG_PATH)
				.addPackage(RELATIONS_PKG_PATH));
	}

	public AArchiveTest() {
		this(new Configuration(new DummyParser(), new DummyLanguage(), new DummyModule(), "localhost"),
				new ConsoleLogger(Logger.getLogger(AArchiveTest.class.getName())));
	}

	@BeforeEach
	@Tag("system")
	public void scanModels() throws ScannerException {
		for (PassiveModule module : this.cnf.getPassiveModules())
			module.configure(this.archive);
	}

	protected String printQuery(Class<?> clazz, IFilter filter) throws Exception {
		this.archive.logPatterns(this.logger);
		return "[QUERY][" + clazz.getSimpleName() + "]\n" + iLanguage.load(filter) + '\n';
	}

	protected String printSave(Object entity, int depth) throws Exception {
		this.archive.logPatterns(this.logger);
		return "[SAVE][" + entity.getClass()
				.getSimpleName() + "]\n"
				+ iLanguage.save(this.archive.save(entity.getClass(), entity, depth)
						.getDataContainer(), null)
						.qry()
				+ '\n';
	}

}
