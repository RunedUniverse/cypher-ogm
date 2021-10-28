/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.libs.rogm.lang.cypher;

import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.lang.cypher.pipeline.chains.CleanupLayers;
import net.runeduniverse.libs.rogm.lang.cypher.pipeline.chains.CypherChains;
import net.runeduniverse.libs.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.modules.IdTypeResolver;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.chain.ChainManager;

public class CypherLanguage implements Language {

	@Override
	public Instance build(final Logger logger, final IdTypeResolver resolver, final Parser.Instance parser) {
		return new CypherInstance(resolver, parser, new UniversalLogger(CypherInstance.class, logger));
	}

	public String getChainLabel() {
		return CypherChains.DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS.LABEL;
	}

	@Override
	public void setupChainManager(ChainManager chainManager) throws Exception {
		chainManager.addChainLayers(CleanupLayers.class);
	}
}
