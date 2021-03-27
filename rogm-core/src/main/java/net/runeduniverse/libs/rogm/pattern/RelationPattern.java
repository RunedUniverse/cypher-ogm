package net.runeduniverse.libs.rogm.pattern;

import static net.runeduniverse.libs.utils.StringUtils.isBlank;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.TargetNode;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.annotations.StartNode;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataNode;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataRelation;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Relation;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class RelationPattern extends APattern {

	@Getter
	private String label = null;
	private Direction direction = null;
	private Field startField = null;
	private Field targetField = null;
	// start eq target
	@Getter
	private boolean stEqTr = false;
	private boolean readonlyStart = false;
	private boolean readonlyTarget = false;

	public RelationPattern(PatternStorage storage, Class<?> type) throws Exception {
		super(storage, type);
		RelationshipEntity typeAnno = this.type.getAnnotation(RelationshipEntity.class);
		this.direction = typeAnno.direction();
		this.label = typeAnno.label();
		_parse(this.type);

		if (this.startField == null)
			throw new Exception("Relation<" + type + "> is missing the @StartNode");
		if (Collection.class.isAssignableFrom(this.startField.getType()))
			throw new Exception("@StartNode of Relation<" + type + "> must not be a Collection");

		if (this.targetField == null)
			throw new Exception("Relation<" + type + "> is missing the @TargetNode");
		if (Collection.class.isAssignableFrom(this.targetField.getType()))
			throw new Exception("@TargetNode of Relation<" + type + "> must not be a Collection");
	}

	private void _parse(Class<?> type) throws Exception {
		for (Field field : type.getDeclaredFields()) {
			field.setAccessible(true);
			if (this.parseId(field))
				continue;

			StartNode startAnno = field.getAnnotation(StartNode.class);
			TargetNode targetAnno = field.getAnnotation(TargetNode.class);

			if (startAnno != null) {
				if (targetAnno != null) {
					this.stEqTr = true;
					this.targetField = field;
				} else
					this.readonlyStart = startAnno.readonly();
				this.startField = field;
				continue;
			}
			if (targetAnno != null) {
				this.readonlyTarget = targetAnno.readonly();
				this.targetField = field;
			}
		}
		this.parseMethods(type);

		if (type.getSuperclass().equals(Object.class))
			return;
		_parse(type.getSuperclass());
	}

	public PatternType getPatternType() {
		return PatternType.RELATION;
	}

	@Override
	public Collection<String> getLabels() {
		return Arrays.asList(this.label);
	}

	public IFilter search(boolean lazy) {
		return _complete(this.storage.getFactory().createRelation(this.direction), lazy);
	}

	public IFilter search(Serializable id, boolean lazy) throws Exception {
		return _complete(this.storage.getFactory().createIdRelation(this.direction, id, this.idConverter), lazy);
	}

	private Relation _complete(Relation relation, boolean lazy) {
		if (!isBlank(this.label))
			relation.getLabels().add(label);

		if (this.stEqTr) {
			IFNode node = this._getNode(this.startField.getType(), relation, lazy);
			relation.setStart(node);
			relation.setTarget(node);
			return relation;
		}

		relation.setStart(this._getNode(this.startField.getType(), relation, lazy));
		relation.setTarget(this._getNode(this.targetField.getType(), relation, lazy));
		relation.setPattern(this);
		relation.setReturned(true);
		return relation;
	}

	public Relation createFilter(IFNode caller, Direction direction) {
		Relation relation = this.storage.getFactory().createRelation(this.direction);
		relation.setPattern(this);
		if (!isBlank(this.label))
			relation.getLabels().add(this.label);

		if (this.stEqTr) {
			relation.setStart(caller);
			relation.setTarget(caller);
			return relation;
		}

		if ((this.direction == Direction.OUTGOING && direction == Direction.INCOMING)
				|| (this.direction == Direction.INCOMING && direction == Direction.OUTGOING)) {
			relation.setTarget(caller);
			relation.setStart(this._getNode(this.startField.getType(), relation, true));
		} else {
			relation.setStart(caller);
			relation.setTarget(this._getNode(this.targetField.getType(), relation, true));
		}
		return relation;
	}

	@Override
	public ISaveContainer save(Object entity, Integer depth) throws Exception {
		Map<Object, IDataContainer> includedData = new HashMap<>();
		return new ISaveContainer() {

			@Override
			public IDataContainer getDataContainer() throws Exception {
				return save(entity, null, direction, includedData, depth);
			}

			@Override
			public void postSave() {
				for (Object object : includedData.keySet())
					try {
						storage.getPattern(object.getClass()).postSave(object);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}

			@Override
			public Set<IFilter> getRelatedFilter() {
				return null;
			}
		};
	}

	@Override
	public IDeleteContainer delete(Object entity) throws Exception {
		IBuffer.Entry entry = this.storage.getBuffer().getEntry(entity);
		if (entry == null)
			throw new Exception("Relation-Entity of type<" + entity.getClass().getName() + "> is not loaded!");
		Relation relation = this.storage.getFactory().createIdRelation(Direction.BIDIRECTIONAL, entry.getId(), null);
		relation.setReturned(true);
		return new DeleteContainer(this, entity, entry.getId(), null, relation);
	}

	public IDataRelation save(Object entity, IDataNode caller, Direction direction,
			Map<Object, IDataContainer> includedData, Integer depth) throws Exception {

		if (entity == null || this.startField.get(entity) == null || this.targetField.get(entity) == null)
			return null;

		if (includedData.containsKey(entity))
			return (IDataRelation) includedData.get(entity);

		this.preSave(entity);

		IDataRelation relation = null;
		if (this.isIdSet(entity)) {
			// update (id)
			relation = this.storage.getFactory().createDataRelation(this.direction, entity);
			relation.setFilterType(FilterType.UPDATE);
		} else {
			// create (!id)
			relation = this.storage.getFactory().createDataRelation(this.direction, entity);
			relation.setFilterType(FilterType.CREATE);
		}
		relation.setReturned(true);
		includedData.put(entity, relation);

		if (!isBlank(this.label))
			relation.getLabels().add(this.label);

		if (caller == null) {
			// Relation gets called first
			caller = _getDataNode(this.startField, entity, includedData, relation, depth);
			relation.setStart(caller);

			if (this.stEqTr)
				relation.setTarget(caller);
			else
				relation.setTarget(_getDataNode(this.targetField, entity, includedData, relation, depth));

			return _savecheck(relation);
		}

		if (this.stEqTr) {
			relation.setStart(caller);
			relation.setTarget(caller);
			return _savecheck(relation);
		}

		if ((this.direction == Direction.OUTGOING && direction == Direction.INCOMING)
				|| (this.direction == Direction.INCOMING && direction == Direction.OUTGOING)) {
			relation.setTarget(caller);
			relation.setStart(
					_getDataNode(this.startField, entity, includedData, relation, this.readonlyStart ? -1 : depth));
		} else {
			relation.setStart(caller);
			relation.setTarget(
					_getDataNode(this.targetField, entity, includedData, relation, this.readonlyTarget ? -1 : depth));
		}
		return _savecheck(relation);
	}

	private IDataRelation _savecheck(IDataRelation relation) {
		if (relation.getStart() == null || relation.getTarget() == null)
			return null;
		return relation;
	}

	public void setStart(Object entity, Object value) {
		try {
			this.startField.set(entity, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void setTarget(Object entity, Object value) {
		try {
			this.targetField.set(entity, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private IFNode _getNode(Class<?> type, IFRelation relation, boolean lazy) {
		NodePattern node = this.storage.getNode(type);
		if (node == null)
			return null;
		return node.search(relation, lazy);
	}

	private IDataNode _getDataNode(Field field, Object entity, Map<Object, IDataContainer> includedData,
			IDataRelation relation, Integer depth) throws Exception {
		NodePattern node = this.storage.getNode(field.getType());
		if (node == null)
			throw new Exception("NodePattern for Field<" + field.toString() + "> undefined!");
		IDataNode dataNode = node.save(field.get(entity), includedData, depth);
		dataNode.getRelations().add(relation);
		return dataNode;
	}

	@Override
	// Irrelevant as the relation will also get deleted
	public void deleteRelations(Object entity, Collection<Object> delEntries) {
	}

}
