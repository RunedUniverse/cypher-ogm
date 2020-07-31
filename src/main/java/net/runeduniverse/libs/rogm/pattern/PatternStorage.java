package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import lombok.Getter;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.IBuffer.LoadState;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDataRecord;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDeleteContainer;
import net.runeduniverse.libs.rogm.pattern.IPattern.IPatternContainer;
import net.runeduniverse.libs.rogm.pattern.IPattern.ISaveContainer;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.util.DataHashMap;
import net.runeduniverse.libs.rogm.util.DataMap;

public class PatternStorage implements IStorage {

	@Getter
	private final Configuration config;
	@Getter
	private final FilterFactory factory;
	@Getter
	private final Parser.Instance parser;
	private final Map<Class<?>, NodePattern> nodes = new HashMap<>();
	private final Map<Class<?>, RelationPattern> relations = new HashMap<>();
	@Getter
	private final IBuffer buffer;

	public PatternStorage(Configuration cnf, Parser.Instance parser) throws Exception {
		this.config = cnf;
		this.factory = new FilterFactory(cnf.getDbType().getModule());
		this.parser = parser;
		this.buffer = cnf.getBuffer().initialize(this);

		Reflections reflections = new Reflections(cnf.getPkgs().toArray(), new TypeAnnotationsScanner(),
				new SubTypesScanner(true));

		for (Class<?> c : reflections.getTypesAnnotatedWith(RelationshipEntity.class))
			this.relations.put(c, new RelationPattern(this, c));
		for (Class<?> c : reflections.getTypesAnnotatedWith(NodeEntity.class))
			this.nodes.put(c, new NodePattern(this, c));
	}

	public NodePattern getNode(Class<?> clazz) {
		return this.nodes.get(clazz);
	}

	public RelationPattern getRelation(Class<?> clazz) {
		return this.relations.get(clazz);
	}

	public IPattern getPattern(Class<?> clazz) throws Exception {
		if (this.nodes.containsKey(clazz))
			return this.nodes.get(clazz);
		if (this.relations.containsKey(clazz))
			return this.relations.get(clazz);
		throw new Exception("Unsupported Class<" + clazz + "> as @Relation found!");
	}

	public boolean isIdSet(Object entity) {
		try {
			return this.getPattern(entity.getClass()).isIdSet(entity);
		} catch (Exception e) {
			return false;
		}
	}

	public IFilter search(Class<?> clazz, boolean lazy) throws Exception {
		return this.getPattern(clazz).search(lazy);
	}

	public IFilter search(Class<?> clazz, Serializable id, boolean lazy) throws Exception {
		return this.getPattern(clazz).search(id, lazy);
	}

	public ISaveContainer save(Object entity, boolean lazy) throws Exception {
		return this.getPattern(entity.getClass()).save(entity, lazy);
	}

	public IDeleteContainer delete(Object entity) throws Exception {
		return this.getPattern(entity.getClass()).delete(entity);
	}

	public Object setId(Object entity, Serializable id) {
		try {
			return this.getPattern(entity.getClass()).setId(entity, id);
		} catch (Exception e) {
		}
		return entity;
	}

	public <T> Collection<T> parse(Class<T> type, IDataRecord record) throws Exception {
		// type || vv
		// IPattern primaryPattern = record.getPrimaryFilter().getPattern();

		List<DataMap<IFilter, IData, DataType>> dataRecords = new ArrayList<>();
		Set<Object> loadedObjects = new HashSet<>();

		// preloads all mentioned nodes
		for (Set<IData> dataList : record.getData()) {
			DataMap<IFilter, IData, DataType> map = new DataHashMap<>();
			dataRecords.add(map);
			for (IData data : dataList) {
				map.put(data.getFilter(), data, DataType.fromFilter(data.getFilter()));
				if (IPatternContainer.identify(data.getFilter()))
					loadedObjects.add(((IPatternContainer) data.getFilter()).getPattern().parse(data, LoadState.get(data.getFilter())));
			}
		}

		for (DataMap<IFilter, IData, DataType> dataMap : dataRecords) {

			dataMap.forEach(DataType.RELATION, (filter, data) -> {
				IFRelation fRelation = (IFRelation) filter;
				String label = fRelation.getPrimaryLabel();
				IFNode fStartNode = fRelation.getStart();
				NodePattern pStartNode = (NodePattern) ((IPatternContainer) fStartNode).getPattern();
				Object eStartNode = this.buffer.getById(dataMap.get(fStartNode).getId(), pStartNode.getType());
				IFNode fTargetNode = fRelation.getTarget();
				NodePattern pTargetNode = (NodePattern) ((IPatternContainer) fTargetNode).getPattern();
				Object eTargetNode = this.buffer.getById(dataMap.get(fTargetNode).getId(), pTargetNode.getType());

				if (!IPatternContainer.identify(fRelation)) {
					pStartNode.setRelation(fRelation.getDirection(), label, eStartNode, eTargetNode);
					pTargetNode.setRelation(Direction.opposing(fRelation.getDirection()), label, eTargetNode,
							eStartNode);
					return;
				}

				// RelationshipEntity
				RelationPattern rel = (RelationPattern) ((IPatternContainer) fRelation).getPattern();
				Object relEntity = this.buffer.getById(data.getId(), rel.getType());

				rel.setStart(relEntity, eStartNode);
				rel.setTarget(relEntity, eTargetNode);

				pStartNode.setRelation(fRelation.getDirection(), label, eStartNode, relEntity);
				pTargetNode.setRelation(Direction.opposing(fRelation.getDirection()), label, eTargetNode, relEntity);
			});
		}

		Set<T> nodes = new HashSet<>();
		for (Serializable primId : record.getIds())
			nodes.add(this.buffer.getById(primId, type));

		for (Object object : loadedObjects)
			this.getPattern(object.getClass()).postLoad(object);

		return nodes;
	}

	private enum DataType {
		NODE, RELATION, UNKNOWN;

		private static DataType fromFilter(IFilter filter) {
			if (filter instanceof IFNode)
				return NODE;
			if (filter instanceof IFRelation)
				return RELATION;
			return UNKNOWN;
		}
	}
}
