/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.rogm.test;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import net.runeduniverse.lib.rogm.Configuration;
import net.runeduniverse.lib.rogm.errors.ScannerException;
import net.runeduniverse.lib.rogm.modules.PassiveModule;
import net.runeduniverse.lib.rogm.pattern.Archive;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.rogm.querying.QueryBuilder;
import net.runeduniverse.lib.rogm.test.dummies.DummyLanguage;
import net.runeduniverse.lib.rogm.test.dummies.DummyModule;
import net.runeduniverse.lib.rogm.test.dummies.DummyParser;

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
