/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.libs.rogm.test;

import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.Archive;

public abstract class AConfigTest {

	static {
		Archive.PACKAGE_SCANNER_DEBUG_MODE = true;
	}

	public static final String MODEL_PKG_PATH = "net.runeduniverse.libs.rogm.test.model";
	public static final String RELATIONS_PKG_PATH = "net.runeduniverse.libs.rogm.test.model.relations";

	protected final Configuration cnf;
	protected final Logger logger;
	protected final IBuffer buffer;
	// Builder
	protected final Parser parser;
	protected final Language language;
	protected final Module module;
	// Instances
	protected final Parser.Instance iParser;
	protected final Language.Instance iLanguage;
	protected final Module.Instance<?> iModule;

	public AConfigTest(Configuration cnf) {
		this.cnf = cnf.addClassLoader(this.getClass()
				.getClassLoader());
		this.logger = this.cnf.getLogger();
		this.buffer = this.cnf.getBuffer();
		// Builder
		this.parser = this.cnf.getParser();
		this.language = this.cnf.getLang();
		this.module = this.cnf.getModule();
		// Instances
		this.iParser = this.parser == null ? null : this.parser.build(this.logger, this.module);
		this.iLanguage = this.language == null ? null : this.language.build(this.logger, this.module, this.iParser);
		this.iModule = this.module == null ? null : this.module.build(this.logger, this.iParser);
	}

}
