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
package net.runeduniverse.libs.rogm.modules.neo4j;

import java.util.Collection;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.errors.ExceptionSuppressions;
import net.runeduniverse.libs.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pipeline.chain.Chains;
import net.runeduniverse.libs.rogm.pipeline.chain.data.UpdatedEntryContainer;
import net.runeduniverse.libs.chain.Chain;

public interface DebugChainLayers {

	@Chain(label = Chains.SAVE_CHAIN.ONE.LABEL, layers = { Chains.SAVE_CHAIN.ONE.UPDATE_BUFFER_ENTRIES + 1 })
	public static void debugUpdateBufferedEntries(final Archive archive, final IBuffer buffer,
			final UniversalLogger logger, Collection<UpdatedEntryContainer> collection) throws ExceptionSuppressions {
		for (UpdatedEntryContainer updatedEntry : collection) {
			logger.finer(updatedEntry.toString());
		}
	}
}
