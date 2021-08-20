package net.runeduniverse.libs.rogm.pipeline;

import lombok.Getter;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.info.PackageInfo;
import net.runeduniverse.libs.rogm.info.SessionInfo;
import net.runeduniverse.libs.rogm.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.modules.IdTypeResolver;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainManager;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;

public abstract class APipelineFactory<ROUTER extends AChainRouter> {
	protected final Archive archive;
	@Getter
	protected final ROUTER router;
	protected final UniversalLogger logger;

	protected ChainManager chainManager = null;

	protected APipelineFactory(PackageInfo pkgInfo, IdTypeResolver idTypeResolver, ROUTER router,
			UniversalLogger logger) {
		this.archive = new Archive(pkgInfo, idTypeResolver);
		this.router = router;
		router.initialize(this.archive);
		this.logger = logger;
	}

	// SETUP / CONNECTION

	public void setup(final ChainManager chainManager) throws Exception {
		this.chainManager = chainManager;
		this.setupCallOrder();
	}

	protected void setupCallOrder() throws Exception {
		this.setupChainManager(this.chainManager);
		this.router.setChainManager(this.chainManager);
		this.setupArchive(this.archive);
	}

	protected abstract void setupArchive(Archive archive) throws ScannerException;

	protected abstract void setupChainManager(ChainManager chainManager) throws Exception;

	public abstract boolean isConnected();

	public abstract void closeConnections();

	public abstract void closePipeline(Pipeline closingPipeline);

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

	public QueryBuilder getQueryBuilder() {
		return this.archive.getQueryBuilder();
	}
}
