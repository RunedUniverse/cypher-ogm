package net.runeduniverse.libs.rogm.pipeline.transaction;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.info.SessionInfo;
import net.runeduniverse.libs.rogm.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.pattern.Archive;

@RequiredArgsConstructor
public abstract class ATransactionFactory {
	protected final Archive archive;
	protected final UniversalLogger logger;

	// SETUP / CONNECTION

	public void setup() throws ScannerException {
		this.archive.applyConfig();
	}

	public abstract boolean isConnected();

	public abstract void closeConnections();

	@Override
	protected void finalize() throws Throwable {
		this.closeConnections();
		super.finalize();
	}

	// GETTER

	public UniversalLogger getLogger() {
		return this.logger;
	}

	public abstract SessionInfo getSessionInfo();
}
