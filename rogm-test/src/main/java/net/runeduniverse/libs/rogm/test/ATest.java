package net.runeduniverse.libs.rogm.test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.DatabaseType;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;

public abstract class ATest {

	protected final DatabaseType dbType;
	// Builder
	protected final Parser parser;
	protected final Module module;
	protected final Language language;
	// Instances
	protected final Parser.Instance iParser;
	protected final Module.Instance<?> iModule;
	protected final Language.Instance iLanguage;

	public ATest(DatabaseType dbType) {
		this.dbType = dbType;
		Configuration cnf = new Configuration(dbType, "");
		// Builder
		this.parser = dbType.getParser();
		this.module = dbType.getModule();
		this.language = dbType.getLang();
		// Instances
		this.iParser = this.parser.build(cnf);
		this.iModule = this.module.build(cnf);
		this.iLanguage = this.language.build(this.iParser, this.module);
	}

	public ATest(Configuration cnf) {
		this.dbType = cnf.getDbType();
		// Builder
		this.parser = dbType.getParser();
		this.module = dbType.getModule();
		this.language = dbType.getLang();
		// Instances
		this.iParser = this.parser.build(cnf);
		this.iModule = this.module.build(cnf);
		this.iLanguage = this.language.build(this.iParser, this.module);
	}

}
