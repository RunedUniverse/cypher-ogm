package net.runeduniverse.libs.rogm.pattern;

import static net.runeduniverse.libs.utils.StringUtils.isBlank;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.PostSave;
import net.runeduniverse.libs.rogm.annotations.PreSave;
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

public class RelationPattern extends APattern implements IRelationPattern {

	@Getter
	private String label = null;
	private Direction direction = null;
	private FieldPattern startField = null;
	private FieldPattern targetField = null;
	// start eq target
	@Getter
	private boolean stEqTr = false;
	private boolean readonlyStart = false;
	private boolean readonlyTarget = false;

	public RelationPattern(IStorage factory, String pkg, ClassLoader loader, Class<?> type) {
		super(factory, pkg, loader, type);

		RelationshipEntity typeAnno = this.type.getAnnotation(RelationshipEntity.class);
		this.direction = typeAnno.direction();
		this.label = typeAnno.label();
	}

	@Override
	public void validate() throws Exception {
		super.validate();
		this.startField = super.getField(StartNode.class);

		if (this.startField == null)
			throw new Exception("Relation<" + type + "> is missing the @StartNode");
		if (Collection.class.isAssignableFrom(this.startField.getType()))
			throw new Exception("@StartNode of Relation<" + type + "> must not be a Collection");

		this.targetField = super.getField(TargetNode.class);
		if (this.targetField == null)
			throw new Exception("Relation<" + type + "> is missing the @TargetNode");
		if (Collection.class.isAssignableFrom(this.targetField.getType()))
			throw new Exception("@TargetNode of Relation<" + type + "> must not be a Collection");

		StartNode startAnno = this.startField.getAnno(StartNode.class);
		TargetNode targetAnno = this.targetField.getAnno(TargetNode.class);

		if (this.startField.getField() == this.targetField.getField())
			this.stEqTr = true;

		this.readonlyStart = startAnno.readonly();
		this.readonlyTarget = targetAnno.readonly();
	}

	public PatternType getPatternType() {
		return PatternType.RELATION;
	}

	@Override
	public Collection<String> getLabels() {
		return Arrays.asList(this.label);
	}

	public IFilter search(boolean lazy) {
		return _complete(this.factory.getFactory()
				.createRelation(this.direction), lazy);
	}

	public IFilter search(Serializable id, boolean lazy) throws Exception {
		return _complete(this.factory.getFactory()
				.createIdRelation(this.direction, id, this.idConverter), lazy);
	}

	private Relation _complete(Relation relation, boolean lazy) {
		if (!isBlank(this.label))
			relation.getLabels()
					.add(label);

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
		Relation relation = this.factory.getFactory()
				.createRelation(this.direction);
		relation.setPattern(this);
		if (!isBlank(this.label))
			relation.getLabels()
					.add(this.label);

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
						factory.getPattern(object.getClass())
								.callMethod(PostSave.class, object);
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
		IBuffer.Entry entry = this.factory.getBuffer()
				.getEntry(entity);
		if (entry == null)
			throw new Exception("Relation-Entity of type<" + entity.getClass()
					.getName() + "> is not loaded!");
		Relation relation = this.factory.getFactory()
				.createIdRelation(Direction.BIDIRECTIONAL, entry.getId(), null);
		relation.setReturned(true);
		return new DeleteContainer(this, entity, entry.getId(), null, relation);
	}

	public IDataRelation save(Object entity, IDataNode caller, Direction direction,
			Map<Object, IDataContainer> includedData, Integer depth) throws Exception {

		if (entity == null || this.startField.getValue(entity) == null || this.targetField.getValue(entity) == null)
			return null;

		if (includedData.containsKey(entity))
			return (IDataRelation) includedData.get(entity);

		this.callMethod(PreSave.class, entity);

		IDataRelation relation = null;
		if (this.isIdSet(entity)) {
			// update (id)
			relation = this.factory.getFactory()
					.createDataRelation(this.direction, entity);
			relation.setFilterType(FilterType.UPDATE);
		} else {
			// create (!id)
			relation = this.factory.getFactory()
					.createDataRelation(this.direction, entity);
			relation.setFilterType(FilterType.CREATE);
		}
		relation.setReturned(true);
		includedData.put(entity, relation);

		if (!isBlank(this.label))
			relation.getLabels()
					.add(this.label);

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
			this.startField.setValue(entity, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public void setTarget(Object entity, Object value) {
		try {
			this.targetField.setValue(entity, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private IFNode _getNode(Class<?> type, IFRelation relation, boolean lazy) {
		INodePattern node = this.factory.getNode(type);
		if (node == null)
			return null;
		return node.search(relation, lazy);
	}

	private IDataNode _getDataNode(FieldPattern field, Object entity, Map<Object, IDataContainer> includedData,
			IDataRelation relation, Integer depth) throws Exception {
		INodePattern node = this.factory.getNode(field.getType());
		if (node == null)
			throw new Exception("NodePattern for Field<" + field.toString() + "> undefined!");
		IDataNode dataNode = node.save(field.getValue(entity), includedData, depth);
		dataNode.getRelations()
				.add(relation);
		return dataNode;
	}

}
