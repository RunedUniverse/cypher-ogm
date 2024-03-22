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
package net.runeduniverse.lib.rogm.lang.cypher.pipeline.chains;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import net.runeduniverse.lib.rogm.modules.Module;
import net.runeduniverse.lib.rogm.lang.cypher.CypherInstance;
import net.runeduniverse.lib.rogm.lang.cypher.FilterStatus;
import net.runeduniverse.lib.rogm.lang.cypher.Mapper;
import net.runeduniverse.lib.rogm.querying.IFNode;
import net.runeduniverse.lib.rogm.querying.IFRelation;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.utils.common.DataHashMap;
import net.runeduniverse.lib.utils.common.DataMap;
import net.runeduniverse.lib.utils.chain.Chain;
import net.runeduniverse.lib.utils.chain.ChainRuntime;

public interface CleanupLayers {

	@Chain(label = CypherChains.DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS.LABEL, layers = {
			CypherChains.DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS.REDUCE_LAYERS })
	public static Collection<String> reduceIds(final ChainRuntime<?> runtime, final CypherInstance cypher,
			final Mapper mapper, final Module.Instance<?> module) throws Exception {
		Collection<String> delIds = new HashSet<>();

		for (IFilter qryFilter : mapper.getEffectedQrys()) {
			DataMap<IFilter, String, FilterStatus> effectedMap = new DataHashMap<>();

			for (Map<String, Object> ids : module.query(cypher._load(effectedMap, qryFilter, false))
					.getRawData()) {
				effectedMap.forEach((filter, code) -> {
					if (!(filter instanceof IFNode))
						return;

					for (IFRelation rel : ((IFNode) filter).getRelations()) {
						Object s = ids.get("id_" + effectedMap.get(rel));
						if (s == null)
							continue;
						delIds.add(s.toString());
					}
				});
			}
		}

		delIds.removeAll(mapper.getPersistedIds());
		// Checks for need to clean
		if (delIds.isEmpty())
			runtime.setCanceled(true);
		return delIds;
	}

	@Chain(label = CypherChains.DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS.LABEL, layers = {
			CypherChains.DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS.BUILD_QUERY })
	public static String buildQry(final CypherInstance cypher, Collection<String> ids) {
		return cypher.deleteRelations(ids);
	}

	@Chain(label = CypherChains.DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS.LABEL, layers = {
			CypherChains.DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS.EXECUTE_ON_DATABASE })
	public static void executeQry(final Module.Instance<?> module, String qry) {
		module.execute(qry);
	}
}
