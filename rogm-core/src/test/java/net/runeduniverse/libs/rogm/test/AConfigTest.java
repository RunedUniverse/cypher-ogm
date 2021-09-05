package net.runeduniverse.libs.rogm.test;

import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;

public abstract class AConfigTest {

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
