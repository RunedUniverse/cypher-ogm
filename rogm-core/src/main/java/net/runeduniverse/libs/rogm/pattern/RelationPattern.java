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
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.RelationQueryBuilder;

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

	public RelationPattern(Archive archive, String pkg, ClassLoader loader, Class<?> type) {
		super(archive, pkg, loader, type);

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

	public IQueryBuilder<?, ? extends IFilter> search(boolean lazy) {
		return _complete(this.archive.getQueryBuilder()
				.relation()
				.whereDirection(this.direction), lazy);
		// return _complete(this.factory.getFactory().createRelation(this.direction),
		// lazy);
	}

	public IQueryBuilder<?, ? extends IFilter> search(Serializable id, boolean lazy) throws Exception {
		return _complete(this.archive.getQueryBuilder()
				.relation()
				.whereDirection(this.direction)
				.whereId(id), lazy);
		// return _complete(this.factory.getFactory().createIdRelation(this.direction,
		// id, this.idConverter), lazy);
	}

	private RelationQueryBuilder _complete(RelationQueryBuilder relationBuilder, boolean lazy) {
		if (!isBlank(this.label))
			relationBuilder.getLabels()
					.add(label);

		if (this.stEqTr) {
			NodeQueryBuilder nodeBuilder = this._getNode(this.startField.getType(), relationBuilder, lazy);
			return relationBuilder.setStart(nodeBuilder)
					.setTarget(nodeBuilder);
		}

		return relationBuilder.setStart(this._getNode(this.startField.getType(), relationBuilder, lazy))
				.setTarget(this._getNode(this.targetField.getType(), relationBuilder, lazy))
				.storePattern(this)
				.setReturned(true);
	}

	public RelationQueryBuilder createFilter(NodeQueryBuilder caller, Direction direction) {
		RelationQueryBuilder relationBuilder = this.archive.getQueryBuilder()
				.relation()
				.whereDirection(this.direction)
				.storePattern(this);
		if (!isBlank(this.label))
			relationBuilder.getLabels()
					.add(this.label);

		if (this.stEqTr)
			return relationBuilder.setStart(caller)
					.setTarget(caller);

		if ((this.direction == Direction.OUTGOING && direction == Direction.INCOMING)
				|| (this.direction == Direction.INCOMING && direction == Direction.OUTGOING))
			relationBuilder.setTarget(caller)
					.setStart(this._getNode(this.startField.getType(), relationBuilder, true));
		else
			relationBuilder.setStart(caller)
					.setTarget(this._getNode(this.targetField.getType(), relationBuilder, true));

		return relationBuilder;
	}

	@Override
	public ISaveContainer save(Object entity, Integer depth) throws Exception {
		Map<Object, IQueryBuilder<?, ? extends IFilter>> includedData = new HashMap<>();
		return new ISaveContainer() {

			@Override
			public IDataContainer getDataContainer() throws Exception {
				return (IDataContainer) save(entity, null, direction, includedData, depth).getResult();
			}

			@Override
			public void postSave() {
				for (Object object : includedData.keySet())
					try {
						RelationPattern.this.archive.getPattern(object.getClass(), EntitiyFactory.IAnyPattern.class)
								.callMethod(PostSave.class, object);
						// factory.getPattern(object.getClass()).callMethod(PostSave.class, object);
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

	@SuppressWarnings("deprecation")
	@Override
	public IDeleteContainer delete(Object entity) throws Exception {
		IBuffer.Entry entry = this.archive.getBuffer()
				.getEntry(entity);
		if (entry == null)
			throw new Exception("Relation-Entity of type<" + entity.getClass()
					.getName() + "> is not loaded!");

		// this.factory.getFactory().createIdRelation(Direction.BIDIRECTIONAL,
		// entry.getId(), null);
		// relation.setReturned(true);
		return new DeleteContainer(this, entity, entry.getId(), null, this.archive.getQueryBuilder()
				.relation()
				.whereDirection(Direction.BIDIRECTIONAL)
				.whereId(entry.getId())
				.setReturned(true)
				.getResult());
	}

	public RelationQueryBuilder save(Object entity, NodeQueryBuilder caller, Direction direction,
			Map<Object, IQueryBuilder<?, ? extends IFilter>> includedData, Integer depth) throws Exception {

		if (entity == null || this.startField.getValue(entity) == null || this.targetField.getValue(entity) == null)
			return null;

		if (includedData.containsKey(entity))
			return (RelationQueryBuilder) includedData.get(entity);

		this.callMethod(PreSave.class, entity);

		RelationQueryBuilder relationBuilder = null;
		// TODO simplify #1
		if (this.isIdSet(entity)) {
			// update (id)
			relationBuilder = this.archive.getQueryBuilder()
					.relation()
					.whereDirection(this.direction)
					.storeData(entity);
			relationBuilder.asUpdate();
			// this.factory.getFactory().createDataRelation(this.direction, entity);
			// relationBuilder.setFilterType(FilterType.UPDATE);
		} else {
			// create (!id)
			relationBuilder = this.archive.getQueryBuilder()
					.relation()
					.whereDirection(this.direction)
					.storeData(entity);
			relationBuilder.asWrite();
			// this.factory.getFactory().createDataRelation(this.direction, entity);
			// relationBuilder.setFilterType(FilterType.CREATE);
		}
		// #1 end
		relationBuilder.setReturned(true);
		includedData.put(entity, relationBuilder);

		if (!isBlank(this.label))
			relationBuilder.getLabels()
					.add(this.label);

		if (caller == null) {
			// Relation gets called first
			caller = _getDataNode(this.startField, entity, includedData, relationBuilder, depth);
			relationBuilder.setStart(caller);

			if (this.stEqTr)
				relationBuilder.setTarget(caller);
			else
				relationBuilder.setTarget(_getDataNode(this.targetField, entity, includedData, relationBuilder, depth));

			return _savecheck(relationBuilder);
		}

		if (this.stEqTr) {
			relationBuilder.setStart(caller);
			relationBuilder.setTarget(caller);
			return _savecheck(relationBuilder);
		}

		if ((this.direction == Direction.OUTGOING && direction == Direction.INCOMING)
				|| (this.direction == Direction.INCOMING && direction == Direction.OUTGOING)) {
			relationBuilder.setTarget(caller);
			relationBuilder.setStart(_getDataNode(this.startField, entity, includedData, relationBuilder,
					this.readonlyStart ? -1 : depth));
		} else {
			relationBuilder.setStart(caller);
			relationBuilder.setTarget(_getDataNode(this.targetField, entity, includedData, relationBuilder,
					this.readonlyTarget ? -1 : depth));
		}
		return _savecheck(relationBuilder);
	}

	private RelationQueryBuilder _savecheck(RelationQueryBuilder relationBuilder) {
		if (relationBuilder.getStart() == null || relationBuilder.getTarget() == null)
			return null;
		return relationBuilder;
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

	private NodeQueryBuilder _getNode(Class<?> type, RelationQueryBuilder relation, boolean lazy) {
		INodePattern node = this.archive.getPattern(type, INodePattern.class);
		if (node == null)
			return null;
		return node.search(relation, lazy);
	}

	private NodeQueryBuilder _getDataNode(FieldPattern field, Object entity,
			Map<Object, IQueryBuilder<?, ? extends IFilter>> includedData, RelationQueryBuilder relation, Integer depth)
			throws Exception {
		INodePattern node = this.archive.getPattern(field.getType(), INodePattern.class);
		if (node == null)
			throw new Exception("NodePattern for Field<" + field.toString() + "> undefined!");
		NodeQueryBuilder nodeBuilder = node.save(field.getValue(entity), includedData, depth);
		nodeBuilder.addRelation(relation);
		return nodeBuilder;
	}

}
