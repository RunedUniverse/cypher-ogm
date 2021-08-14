package net.runeduniverse.libs.rogm.pipeline;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.info.SessionInfo;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;

public class DatabaseTransactionFactory extends ATransactionFactory {

	protected final Configuration cnf;
	protected final Parser parser;
	protected final Language lang;
	protected final Module module;
	protected final Parser.Instance parserInstance;
	protected final Language.Instance langInstance;
	protected final Module.Instance<?> moduleInstance;

	public DatabaseTransactionFactory(Configuration config) {
		this(config, new UniversalLogger(DatabaseTransactionFactory.class, config.getLogger()));
	}

	public DatabaseTransactionFactory(Configuration config, UniversalLogger logger) {
		super(config.getPackageInfo(), config.getModule(), new DatabaseTransactionRouter(), logger);
		this.cnf = config;

		this.parser = this.cnf.getParser();
		this.lang = this.cnf.getLang();
		this.module = this.cnf.getModule();

		this.parserInstance = this.parser.build(this.cnf);
		this.moduleInstance = this.module.build(this.cnf);
		this.langInstance = this.lang.build(this.parserInstance, this.module);
	}

	// SETUP / CONNECTION

	@Override
	public void setup() throws ScannerException {
		super.setup();
		if (!this.moduleInstance.connect(this.cnf.getConnectionInfo()))
			this.logger.warning("Failed to establish initial database-connection!");
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void closeConnections() {
		// TODO Auto-generated method stub

	}

	// GETTER

	@Override
	public SessionInfo getSessionInfo() {
		return new SessionInfo(DatabaseTransactionFactory.class, this.cnf.getBuffer()
				.getClass(), this.cnf.getPackageInfo(), this.cnf.getConnectionInfo());
	}

}
