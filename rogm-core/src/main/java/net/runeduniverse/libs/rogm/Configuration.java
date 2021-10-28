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
package net.runeduniverse.libs.rogm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.buffer.BasicBuffer;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.info.ConnectionInfo;
import net.runeduniverse.libs.rogm.info.PackageInfo;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.PassiveModule;
import net.runeduniverse.libs.rogm.parser.Parser;

@Getter
public class Configuration {

	protected final Set<String> pkgs = new HashSet<>();
	protected final Set<ClassLoader> loader = new HashSet<>();
	protected final Set<PassiveModule> passiveModules = new HashSet<>();
	protected final Parser parser;
	protected final Language lang;
	protected final Module module;

	@Setter
	protected String uri;
	protected Logger logger = null;
	@Setter
	protected Level loggingLevel = null;
	@Setter
	protected String protocol;
	@Setter
	protected int port;
	@Setter
	protected String user;
	@Setter
	protected String password;
	@Setter
	protected IBuffer buffer = new BasicBuffer();

	public Configuration(Parser parser, Language lang, Module module, String uri) throws NullPointerException {
		this.parser = parser;
		this.lang = lang;
		this.module = module;
		this.passiveModules.add(this.module);
		this.uri = uri;

		this.validate();
	}

	public void validate() {
		if (this.parser == null)
			throw new NullPointerException("Instance of net.runeduniverse.libs.rogm.parser.Parser is missing!");
		if (this.lang == null)
			throw new NullPointerException("Instance of net.runeduniverse.libs.rogm.lang.Language is missing!");
		if (this.module == null)
			throw new NullPointerException("Instance of net.runeduniverse.libs.rogm.modules.Module is missing!");
	}

	public Configuration addPackage(String pkg) {
		this.pkgs.add(pkg);
		return this;
	}

	public Configuration addPackage(List<String> pkgs) {
		this.pkgs.addAll(pkgs);
		return this;
	}

	public Configuration addClassLoader(ClassLoader loader) {
		this.loader.add(loader);
		return this;
	}

	public Configuration addClassLoader(List<ClassLoader> loader) {
		this.loader.addAll(loader);
		return this;
	}

	public Configuration addPassiveModule(PassiveModule passivemodule) {
		this.passiveModules.add(passivemodule);
		return this;
	}

	public Configuration setLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	public Level getLoggingLevel() {
		if (this.loggingLevel != null)
			return this.loggingLevel;
		return this.logger == null ? Level.INFO : this.logger.getLevel();
	}

	public PackageInfo getPackageInfo() {
		return new PackageInfo(this);
	}

	public ConnectionInfo getConnectionInfo() {
		return new ConnectionInfo(this);
	}
}
