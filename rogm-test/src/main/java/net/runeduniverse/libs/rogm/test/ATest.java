package net.runeduniverse.libs.rogm.test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;

public abstract class ATest {

	protected final Configuration cnf;
	// Builder
	protected final Parser parser;
	protected final Module module;
	protected final Language language;
	// Instances
	protected final Parser.Instance iParser;
	protected final Module.Instance<?> iModule;
	protected final Language.Instance iLanguage;

	public ATest(Configuration cnf) {
		this.cnf = cnf;
		// Builder
		this.parser = this.cnf.getParser();
		this.module = this.cnf.getModule();
		this.language = this.cnf.getLang();
		// Instances
		this.iParser = this.parser == null ? null : this.parser.build(this.cnf);
		this.iModule = this.module == null ? null : this.module.build(this.cnf);
		this.iLanguage = this.language == null ? null : this.language.build(this.iParser, this.module);
	}

}
