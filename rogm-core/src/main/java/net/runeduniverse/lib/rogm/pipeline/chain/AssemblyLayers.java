/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.rogm.pipeline.chain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.runeduniverse.lib.rogm.annotations.Direction;
import net.runeduniverse.lib.rogm.annotations.PostLoad;
import net.runeduniverse.lib.rogm.annotations.PostReload;
import net.runeduniverse.lib.rogm.buffer.BufferTypes;
import net.runeduniverse.lib.rogm.buffer.IBuffer;
import net.runeduniverse.lib.rogm.pattern.Archive;
import net.runeduniverse.lib.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.lib.rogm.pattern.IPattern;
import net.runeduniverse.lib.rogm.pattern.NodePattern;
import net.runeduniverse.lib.rogm.pattern.RelationPattern;
import net.runeduniverse.lib.rogm.pattern.IPattern.IData;
import net.runeduniverse.lib.rogm.pattern.IPattern.IDataRecord;
import net.runeduniverse.lib.rogm.pattern.IPattern.IPatternContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.RelatedEntriesContainer;
import net.runeduniverse.lib.rogm.querying.IFNode;
import net.runeduniverse.lib.rogm.querying.IFRelation;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.utils.chain.Chain;
import net.runeduniverse.lib.utils.chain.ChainRuntime;
import net.runeduniverse.lib.utils.common.DataHashMap;
import net.runeduniverse.lib.utils.common.api.DataMap;

public interface AssemblyLayers extends BufferTypes {

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = { Chains.LOAD_CHAIN.ALL.ASSEMBLY_ENTITY_COLLECTION })
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.ASSEMBLY_ENTITY_COLLECTION })
	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL,
			layers = { Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.ASSEMBLY_ENTITY_COLLECTION })
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> load(ChainRuntime<?> runtime, Archive archive, IBuffer buffer, IDataRecord record)
			throws Exception {
		// type || vv
		final Class<?> returnType;
		IFilter primaryFilter = record.getPrimaryFilter();
		if ((runtime.getResultType() == Object.class || runtime.getResultType() == Collection.class)
				&& primaryFilter != null && primaryFilter instanceof IPatternContainer) {
			IPattern primaryPattern = ((IPatternContainer) primaryFilter).getPattern();
			returnType = primaryPattern.getType();
		} else
			returnType = runtime.getResultType();

		List<DataMap<IFilter, IData, DataType>> dataRecords = new ArrayList<>();
		Set<Object> loadedObjects = new HashSet<>();

		// preloads all mentioned nodes
		for (Set<IData> dataList : record.getData()) {
			DataMap<IFilter, IData, DataType> map = new DataHashMap<>();
			dataRecords.add(map);
			for (IData data : dataList) {
				IFilter dataFilter = data.getFilter();
				map.put(dataFilter, data, DataType.fromFilter(dataFilter));
				if (IPatternContainer.identify(dataFilter)) {
					IBaseQueryPattern<?> dataPattern = ((IPatternContainer) dataFilter).getPattern();
					loadedObjects.add(runtime.callSubChainWithRuntimeData(Chains.BUFFER_CHAIN.LOAD.LABEL,
							dataPattern.getType(), data, dataPattern));
				}
			}
		}

		for (DataMap<IFilter, IData, DataType> dataMap : dataRecords)
			dataMap.forEach(DataType.RELATION, (filter, data) -> {
				IFRelation fRelation = (IFRelation) filter;
				String label = fRelation.getPrimaryLabel();
				IFNode fStartNode = fRelation.getStart();
				NodePattern pStartNode = (NodePattern) ((IPatternContainer) fStartNode).getPattern();
				Object eStartNode = buffer.getById(dataMap.get(fStartNode)
						.getId(), pStartNode.getType());
				IFNode fTargetNode = fRelation.getTarget();
				NodePattern pTargetNode = (NodePattern) ((IPatternContainer) fTargetNode).getPattern();
				Object eTargetNode = buffer.getById(dataMap.get(fTargetNode)
						.getId(), pTargetNode.getType());

				if (!IPatternContainer.identify(fRelation)) {
					pStartNode.setRelation(fRelation.getDirection(), label, eStartNode, eTargetNode);
					pTargetNode.setRelation(Direction.opposing(fRelation.getDirection()), label, eTargetNode,
							eStartNode);
					return;
				}

				// RelationshipEntity
				RelationPattern rel = (RelationPattern) ((IPatternContainer) fRelation).getPattern();
				Object relEntity = buffer.getById(data.getId(), rel.getType());

				rel.setStart(relEntity, eStartNode);
				rel.setTarget(relEntity, eTargetNode);

				pStartNode.setRelation(fRelation.getDirection(), label, eStartNode, relEntity);
				pTargetNode.setRelation(Direction.opposing(fRelation.getDirection()), label, eTargetNode, relEntity);
			});

		Set<T> nodes = new HashSet<>();
		if (returnType != null) {
			for (Serializable primId : record.getIds()) {
				Object o = buffer.getById(primId, returnType);
				if (o != null)
					nodes.add((T) o);
			}
			runtime.setPossibleResult(nodes);
		}

		for (Object object : loadedObjects)
			archive.callMethod(object.getClass(), PostLoad.class, object);
		return nodes;
	}

	@Chain(label = Chains.RELOAD_CHAIN.SELECTED.LABEL,
			layers = { Chains.RELOAD_CHAIN.SELECTED.UPDATE_ENTITY_COLLECTION })
	public static void update(ChainRuntime<?> runtime, Archive archive, IBuffer buffer, EntityContainer entityContainer,
			RelatedEntriesContainer relatedEntities, IDataRecord record) throws Exception {
		Object entity = entityContainer.getEntity();
		List<DataMap<IFilter, IData, DataType>> dataRecords = new ArrayList<>();

		// preloads all mentioned nodes + update @Property and @Id through IBuffer
		for (Set<IData> dataList : record.getData()) {
			DataMap<IFilter, IData, DataType> map = new DataHashMap<>();
			dataRecords.add(map);
			for (IData data : dataList) {
				IFilter dataFilter = data.getFilter();
				DataType dtype = DataType.fromFilter(dataFilter);
				map.put(data.getFilter(), data, dtype);

				if (IPatternContainer.identify(dataFilter) && LoadState.get(dataFilter) == LoadState.LAZY) {
					IBaseQueryPattern<?> dataPattern = ((IPatternContainer) dataFilter).getPattern();
					IEntry entry = runtime.callSubChainWithSourceData(Chains.BUFFER_CHAIN.UPDATE.LABEL, IEntry.class,
							data, dataPattern);

					if (entry.getEntity() != entity && dtype != DataType.RELATION)
						relatedEntities.addEntry(entry);
				}
			}
		}

		for (DataMap<IFilter, IData, DataType> dataMap : dataRecords)
			dataMap.forEach(DataType.RELATION, (filter, data) -> {
				IFRelation fRelation = (IFRelation) filter;
				String label = fRelation.getPrimaryLabel();
				IFNode fStartNode = fRelation.getStart();
				NodePattern pStartNode = (NodePattern) ((IPatternContainer) fStartNode).getPattern();
				Object eStartNode = buffer.getById(dataMap.get(fStartNode)
						.getId(), pStartNode.getType());
				IFNode fTargetNode = fRelation.getTarget();
				NodePattern pTargetNode = (NodePattern) ((IPatternContainer) fTargetNode).getPattern();
				Object eTargetNode = buffer.getById(dataMap.get(fTargetNode)
						.getId(), pTargetNode.getType());

				pStartNode.deleteRelations(eStartNode);
				pTargetNode.deleteRelations(eTargetNode);

				if (!IPatternContainer.identify(fRelation)) {
					pStartNode.setRelation(fRelation.getDirection(), label, eStartNode, eTargetNode);
					pTargetNode.setRelation(Direction.opposing(fRelation.getDirection()), label, eTargetNode,
							eStartNode);
					return;
				}

				// RelationshipEntity
				RelationPattern rel = (RelationPattern) ((IPatternContainer) fRelation).getPattern();
				Object relEntity = buffer.getById(data.getId(), rel.getType());

				rel.setStart(relEntity, null);
				rel.setTarget(relEntity, null);

				rel.setStart(relEntity, eStartNode);
				rel.setTarget(relEntity, eTargetNode);

				pStartNode.setRelation(fRelation.getDirection(), label, eStartNode, relEntity);
				pTargetNode.setRelation(Direction.opposing(fRelation.getDirection()), label, eTargetNode, relEntity);
			});

		archive.callMethod(entity.getClass(), PostReload.class, entity);
	}

	public enum DataType {
		NODE, RELATION, UNKNOWN;

		static DataType fromFilter(IFilter filter) {
			if (filter instanceof IFNode)
				return NODE;
			if (filter instanceof IFRelation)
				return RELATION;
			return UNKNOWN;
		}
	}
}
