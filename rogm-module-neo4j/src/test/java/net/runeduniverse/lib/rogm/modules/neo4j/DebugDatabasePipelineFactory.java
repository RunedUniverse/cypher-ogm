/*
 * Copyright © 2023 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.rogm.modules.neo4j;

import net.runeduniverse.lib.rogm.Configuration;
import net.runeduniverse.lib.rogm.pipeline.DatabasePipelineFactory;
import net.runeduniverse.lib.utils.chain.ChainManager;

public class DebugDatabasePipelineFactory extends DatabasePipelineFactory {

	public DebugDatabasePipelineFactory(Configuration config) {
		super(config);
	}

	@Override
	protected void setupChainManager(ChainManager chainManager) throws Exception {
		chainManager.addChainLayers(DebugChainLayers.class);
		super.setupChainManager(chainManager);
	}

}
