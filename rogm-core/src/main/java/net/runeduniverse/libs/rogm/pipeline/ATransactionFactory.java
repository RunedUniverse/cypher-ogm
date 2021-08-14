package net.runeduniverse.libs.rogm.pipeline;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.info.PackageInfo;
import net.runeduniverse.libs.rogm.info.SessionInfo;
import net.runeduniverse.libs.rogm.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.modules.IdTypeResolver;
import net.runeduniverse.libs.rogm.pattern.Archive;

public abstract class ATransactionFactory {
	protected final Archive archive;
	protected final ATransactionRouter router;
	protected final UniversalLogger logger;

	protected ATransactionFactory(PackageInfo pkgInfo, IdTypeResolver idTypeResolver, ATransactionRouter router,
			UniversalLogger logger) {
		this.archive = new Archive(pkgInfo, idTypeResolver);
		this.router = router;
		this.logger = logger;
	}

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
