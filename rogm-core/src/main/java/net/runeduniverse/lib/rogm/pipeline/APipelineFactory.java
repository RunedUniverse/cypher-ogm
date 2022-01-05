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
package net.runeduniverse.lib.rogm.pipeline;

import lombok.Getter;
import net.runeduniverse.lib.rogm.Session;
import net.runeduniverse.lib.rogm.errors.ScannerException;
import net.runeduniverse.lib.rogm.info.PackageInfo;
import net.runeduniverse.lib.rogm.info.SessionInfo;
import net.runeduniverse.lib.rogm.modules.IdTypeResolver;
import net.runeduniverse.lib.rogm.pattern.Archive;
import net.runeduniverse.lib.rogm.querying.QueryBuilder;
import net.runeduniverse.lib.utils.chain.ChainManager;
import net.runeduniverse.lib.utils.logging.UniversalLogger;

public abstract class APipelineFactory<ROUTER extends AChainRouter> {
	protected final Archive archive;
	@Getter
	protected final ROUTER router;
	protected final UniversalLogger logger;

	protected Pipeline pipeline = null;
	protected ChainManager chainManager = null;

	protected APipelineFactory(PackageInfo pkgInfo, IdTypeResolver idTypeResolver, ROUTER router,
			UniversalLogger logger) {
		this.archive = new Archive(pkgInfo, idTypeResolver);
		this.router = router;
		router.initialize(this.archive);
		this.logger = logger;
	}

	// SETUP / CONNECTION

	public void setup(final Pipeline pipeline, final ChainManager chainManager) throws Exception {
		this.pipeline = pipeline;
		this.chainManager = chainManager;
		this.setupCallOrder();
	}

	protected void setupCallOrder() throws Exception {
		this.setupChainManager(this.chainManager);
		this.router.setChainManager(this.chainManager);
		this.setupArchive(this.archive);
		this.archive.logPatterns(this.logger);
	}

	protected abstract void setupArchive(Archive archive) throws ScannerException;

	protected abstract void setupChainManager(ChainManager chainManager) throws Exception;

	@SuppressWarnings("deprecation")
	public Session buildSession() {
		SessionWrapper wrapper = new SessionWrapper(this.pipeline, this, this.pipeline.getLogger(),
				this.getSessionInfo());
		this.pipeline.registerActiveSession(wrapper);
		return wrapper;
	}

	public abstract boolean isConnected();

	public abstract void closeConnections();

	@SuppressWarnings("deprecation")
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
