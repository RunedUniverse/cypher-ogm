package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.PostLoad;
import net.runeduniverse.libs.rogm.annotations.PostReload;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.IBuffer.Entry;
import net.runeduniverse.libs.rogm.buffer.IBuffer.LoadState;
import net.runeduniverse.libs.rogm.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDataRecord;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDeleteContainer;
import net.runeduniverse.libs.rogm.pattern.IPattern.IPatternContainer;
import net.runeduniverse.libs.rogm.pattern.IPattern.ISaveContainer;
import net.runeduniverse.libs.rogm.pattern.IPattern.PatternType;
import net.runeduniverse.libs.rogm.pattern.scanner.TypeScanner;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.scanner.PackageScanner;
import net.runeduniverse.libs.utils.DataHashMap;
import net.runeduniverse.libs.utils.DataMap;
import net.runeduniverse.libs.utils.DataMap.Value;

public class EntitiyFactory implements IStorage {

	@Getter
	private final Configuration config;
	@Getter
	private final FilterFactory factory;
	@Getter
	private final Parser.Instance parser;
	@Getter
	private final IBuffer buffer;
	@Getter
	private final UniversalLogger logger;

	private final DataMap<Class<?>, IPattern, PatternType> patterns = new DataHashMap<>();

	public EntitiyFactory(Configuration cnf, Parser.Instance parser) throws Exception {
		this.config = cnf;
		this.logger = new UniversalLogger(EntitiyFactory.class, cnf.getLogger());
		this.factory = new FilterFactory(cnf.getModule());
		this.parser = parser;
		this.buffer = cnf.getBuffer()
				.initialize(this);

		new PackageScanner().includeOptions(cnf.getLoader(), cnf.getPkgs(), cnf.getScanner(),
				new TypeScanner.NodeScanner(this, p -> patterns.put(p.getType(), (IPattern) p, PatternType.NODE)),
				new TypeScanner.RelationScanner(this,
						p -> patterns.put(p.getType(), (IPattern) p, PatternType.RELATION)),
				new PackageScanner.Validator() {

					@Override
					public void validate() throws Exception {
						EntitiyFactory.this.validate(PatternType.NODE);
						EntitiyFactory.this.validate(PatternType.RELATION);
						EntitiyFactory.this.validate(PatternType.ADAPTER);
						EntitiyFactory.this.validate(PatternType.UNKNOWN);
					}
				})
				.scan()
				.throwSurpressions(new Exception("Pattern parsing failed! See surpressed Exceptions!"));

		this.logPatterns("Nodes", patterns, PatternType.NODE);
		this.logPatterns("Relations", patterns, PatternType.RELATION);
	}

	public INodePattern getNode(Class<?> clazz) {
		if (this.patterns.getData(clazz) == PatternType.NODE)
			return (INodePattern) this.patterns.get(clazz);
		return null;
	}

	public IRelationPattern getRelation(Class<?> clazz) {
		if (this.patterns.getData(clazz) == PatternType.RELATION)
			return (IRelationPattern) this.patterns.get(clazz);
		return null;
	}

	public IPattern getPattern(Class<?> clazz) throws Exception {
		if (this.patterns.containsKey(clazz))
			return this.patterns.get(clazz);
		throw logger.throwing("getPattern(Class<?>)", new Exception("Unsupported Entity-Class <" + clazz + "> found!"));
	}

	public boolean isIdSet(Object entity) {
		try {
			return this.getPattern(entity.getClass())
					.isIdSet(entity);
		} catch (Exception e) {
			this.logger.burying("isIdSet(Object)", e);
			return false;
		}
	}

	public IFilter search(Class<?> clazz, boolean lazy) throws Exception {
		return this.getPattern(clazz)
				.search(lazy);
	}

	public IFilter search(Class<?> clazz, Serializable id, boolean lazy) throws Exception {
		return this.getPattern(clazz)
				.search(id, lazy);
	}

	public IFilter search(Object entity, boolean lazy) throws Exception {
		IBuffer.Entry entry = this.buffer.getEntry(entity);
		return entry.getPattern()
				.search(entry.getId(), lazy);
	}

	public ISaveContainer save(Object entity, Integer depth) throws Exception {
		return this.getPattern(entity.getClass())
				.save(entity, depth);
	}

	public IDeleteContainer delete(Object entity) throws Exception {
		return this.getPattern(entity.getClass())
				.delete(entity);
	}

	public Object setId(Object entity, Serializable id) {
		try {
			return this.getPattern(entity.getClass())
					.setId(entity, id);
		} catch (Exception e) {
			this.logger.burying("setId(Object, Serializable)", e);
		}
		return entity;
	}

	public <T> Collection<T> parse(Class<T> type, IDataRecord record, Set<Entry> lazyEntries) throws Exception {
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
					loadedObjects.add(((IPatternContainer) data.getFilter()).getPattern()
							.parse(data, LoadState.get(data.getFilter()), lazyEntries));
			}
		}

		for (DataMap<IFilter, IData, DataType> dataMap : dataRecords)
			dataMap.forEach(DataType.RELATION, (filter, data) -> {
				IFRelation fRelation = (IFRelation) filter;
				String label = fRelation.getPrimaryLabel();
				IFNode fStartNode = fRelation.getStart();
				NodePattern pStartNode = (NodePattern) ((IPatternContainer) fStartNode).getPattern();
				Object eStartNode = this.buffer.getById(dataMap.get(fStartNode)
						.getId(), pStartNode.getType());
				IFNode fTargetNode = fRelation.getTarget();
				NodePattern pTargetNode = (NodePattern) ((IPatternContainer) fTargetNode).getPattern();
				Object eTargetNode = this.buffer.getById(dataMap.get(fTargetNode)
						.getId(), pTargetNode.getType());

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

		Set<T> nodes = new HashSet<>();
		for (Serializable primId : record.getIds())
			nodes.add(this.buffer.getById(primId, type));

		for (Object object : loadedObjects)
			this.getPattern(object.getClass())
					.callMethod(PostLoad.class, object);

		return nodes;
	}

	@Override
	public void update(Object entity, IDataRecord record, Set<Entry> relatedEntities) throws Exception {
		List<DataMap<IFilter, IData, DataType>> dataRecords = new ArrayList<>();

		// preloads all mentioned nodes + update @Property and @Id through IBuffer
		for (Set<IData> dataList : record.getData()) {
			DataMap<IFilter, IData, DataType> map = new DataHashMap<>();
			dataRecords.add(map);
			for (IData data : dataList) {
				DataType dtype = DataType.fromFilter(data.getFilter());
				map.put(data.getFilter(), data, dtype);

				if (IPatternContainer.identify(data.getFilter()) && LoadState.get(data.getFilter()) == LoadState.LAZY) {
					Entry entry = ((IPatternContainer) data.getFilter()).getPattern()
							.update(data);
					if (entry.getEntity() != entity && dtype != DataType.RELATION)
						relatedEntities.add(entry);
				}
			}
		}

		for (DataMap<IFilter, IData, DataType> dataMap : dataRecords)
			dataMap.forEach(DataType.RELATION, (filter, data) -> {
				IFRelation fRelation = (IFRelation) filter;
				String label = fRelation.getPrimaryLabel();
				IFNode fStartNode = fRelation.getStart();
				NodePattern pStartNode = (NodePattern) ((IPatternContainer) fStartNode).getPattern();
				Object eStartNode = this.buffer.getById(dataMap.get(fStartNode)
						.getId(), pStartNode.getType());
				IFNode fTargetNode = fRelation.getTarget();
				NodePattern pTargetNode = (NodePattern) ((IPatternContainer) fTargetNode).getPattern();
				Object eTargetNode = this.buffer.getById(dataMap.get(fTargetNode)
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
				Object relEntity = this.buffer.getById(data.getId(), rel.getType());

				rel.setStart(relEntity, null);
				rel.setTarget(relEntity, null);

				rel.setStart(relEntity, eStartNode);
				rel.setTarget(relEntity, eTargetNode);

				pStartNode.setRelation(fRelation.getDirection(), label, eStartNode, relEntity);
				pTargetNode.setRelation(Direction.opposing(fRelation.getDirection()), label, eTargetNode, relEntity);
			});

		this.getPattern(entity.getClass())
				.callMethod(PostReload.class, entity);
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

	private void logPatterns(String name, DataMap<Class<?>, IPattern, PatternType> patterns, PatternType type) {
		StringBuilder msg = new StringBuilder(name + ':');
		patterns.forEach(type, (c, p) -> {
			msg.append("\n - " + c.getCanonicalName());
		});
		this.logger.finer(msg.toString());
	}

	private void validate(PatternType patternType) throws Exception {
		for (Value<IPattern, PatternType> value : EntitiyFactory.this.patterns.valueSet())
			if (patternType.equals(value.getData()))
				IValidatable.validate(value.getValue());
	}
}
