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
package net.runeduniverse.libs.rogm.pipeline.chain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.runeduniverse.libs.chain.Chain;
import net.runeduniverse.libs.chain.ChainRuntime;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.PostLoad;
import net.runeduniverse.libs.rogm.annotations.PostReload;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes;
import net.runeduniverse.libs.rogm.pattern.NodePattern;
import net.runeduniverse.libs.rogm.pattern.RelationPattern;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDataRecord;
import net.runeduniverse.libs.rogm.pattern.IPattern.IPatternContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.RelatedEntriesContainer;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.utils.DataHashMap;
import net.runeduniverse.libs.utils.DataMap;

public interface AssemblyLayers extends InternalBufferTypes {

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = { Chains.LOAD_CHAIN.ALL.ASSEMBLY_ENTITY_COLLECTION })
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.ASSEMBLY_ENTITY_COLLECTION })
	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.ASSEMBLY_ENTITY_COLLECTION })
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

	@Chain(label = Chains.RELOAD_CHAIN.SELECTED.LABEL, layers = {
			Chains.RELOAD_CHAIN.SELECTED.UPDATE_ENTITY_COLLECTION })
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
					Entry entry = runtime.callSubChainWithSourceData(Chains.BUFFER_CHAIN.UPDATE.LABEL, Entry.class,
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
