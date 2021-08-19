package net.runeduniverse.libs.rogm.pipeline.chain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.PostLoad;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.pattern.NodePattern;
import net.runeduniverse.libs.rogm.pattern.RelationPattern;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDataRecord;
import net.runeduniverse.libs.rogm.pattern.IPattern.IPatternContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.Result;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Store;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.utils.DataHashMap;
import net.runeduniverse.libs.utils.DataMap;

public interface Assembler {

	@Chain(label = Chain.BUFFER_LOAD_CHAIN, layers = { 10 })
	public static void prepareDataForBuffer(IPattern pattern, IData data) {
		pattern.prepareEntityId(data);
	}

	@Chain(label = Chain.LOAD_ALL_CHAIN, layers = { 400 }) // TODO FIX layers
	@Chain(label = Chain.LOAD_ONE_CHAIN, layers = { 400 }) // TODO FIX layers
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> parse(Store store, Result<T> resultType, Archive archive, IBuffer buffer,
			IDataRecord record) throws Exception {
		// type || vv
		final Class<?> returnType;
		IFilter primaryFilter = record.getPrimaryFilter();
		if (resultType.getType() == null && primaryFilter != null && primaryFilter instanceof IPatternContainer) {
			IPattern primaryPattern = ((IPatternContainer) primaryFilter).getPattern();
			returnType = primaryPattern.getType();
		} else
			returnType = resultType.getType();

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
					IPattern dataPattern = ((IPatternContainer) dataFilter).getPattern();
					loadedObjects.add(ChainManager.callChain(Chain.BUFFER_LOAD_CHAIN, dataPattern.getType(), store,
							data, dataPattern));
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
		for (Serializable primId : record.getIds())
			nodes.add((T) buffer.getById(primId, returnType));

		for (Object object : loadedObjects)
			archive.callMethod(object.getClass(), PostLoad.class, object);

		return nodes;
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
