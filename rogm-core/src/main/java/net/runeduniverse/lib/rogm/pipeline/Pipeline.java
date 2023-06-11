/*
 * Copyright © 2022 VenaNocta (venanocta@gmail.com)
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

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.lib.rogm.Session;
import net.runeduniverse.lib.rogm.logging.PipelineLogger;
import net.runeduniverse.lib.utils.chain.ChainManager;

public final class Pipeline implements AutoCloseable {
	private final APipelineFactory<?> factory;
	private final ChainManager chain;
	@Getter
	private final PipelineLogger logger;
	private boolean setupMissing = true;
	private Set<Session> activeSessions = new HashSet<>();

	public Pipeline(APipelineFactory<?> pipelineFactory) {
		this.factory = pipelineFactory;
		this.logger = new PipelineLogger(Pipeline.class, this.factory.getLogger());
		this.chain = new ChainManager(this.logger);
	}

	public Session buildSession() throws Exception {
		if (setupMissing) {
			this.factory.setup(this, this.chain);
			this.setupMissing = false;
		}
		return this.factory.buildSession();
	}

	/**
	 * registers a <code>Session</code> as active
	 * 
	 * @deprecated for internal use only!
	 * @param session registers a session to be managed automatically
	 */
	@Deprecated
	public void registerActiveSession(Session session) {
		this.activeSessions.add(session);
	}

	@Override
	public void close() throws Exception {
		this.factory.closeConnections();
	}

	public void closeConnections(SessionWrapper sessionWrapper) throws Exception {
		this.activeSessions.remove(sessionWrapper);
		if (this.activeSessions.isEmpty())
			this.close();
	}
}
