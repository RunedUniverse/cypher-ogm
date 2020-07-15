package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static net.runeduniverse.libs.rogm.util.Utils.isBlank;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.EndNode;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.annotations.StartNode;
import net.runeduniverse.libs.rogm.lang.Language.DataFilter;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataNode;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataRelation;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Relation;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.util.Buffer;

public class RelationPattern extends APattern {

	private String label = null;
	private Direction direction = null;
	private Field startField = null;
	private Field targetField = null;
	// start eq target
	@Getter
	private boolean stEqTr = false;

	public RelationPattern(PatternStorage storage, Class<?> type) {
		super(storage, type);
		RelationshipEntity typeAnno = this.type.getAnnotation(RelationshipEntity.class);
		this.direction = typeAnno.direction();
		this.label = typeAnno.label();
		_parse(this.type);
	}

	@Override
	public Buffer getBuffer() {
		return this.storage.getRelationBuffer();
	}

	private void _parse(Class<?> type) {
		for (Field field : type.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Id.class) && this.idField == null) {
				this.idField = field;
				continue;
			}

			if (field.isAnnotationPresent(StartNode.class)) {
				if (field.isAnnotationPresent(EndNode.class)) {
					this.stEqTr = true;
					this.targetField = field;
				}
				this.startField = field;
				continue;
			}
			if (field.isAnnotationPresent(EndNode.class))
				this.targetField = field;
		}
		if (type.getSuperclass().equals(Object.class))
			return;
		_parse(type.getSuperclass());
	}

	public IFilter createFilter() {
		return _complete(this.storage.getFactory().createRelation(this.direction));
	}

	public IFilter createIdFilter(Serializable id) throws Exception {
		return _complete(this.storage.getFactory().createIdRelation(this.direction, id));
	}

	private Relation _complete(Relation relation) {
		if (!isBlank(this.label))
			relation.getLabels().add(label);

		if (this.stEqTr) {
			IFNode node = this._getNode(this.startField.getType(), relation);
			relation.setStart(node);
			relation.setTarget(node);
			return relation;
		}

		relation.setStart(this._getNode(this.startField.getType(), relation));
		relation.setTarget(this._getNode(this.targetField.getType(), relation));
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
			relation.setStart(this._getNode(this.startField.getType(), relation));
		} else {
			relation.setStart(caller);
			relation.setTarget(this._getNode(this.targetField.getType(), relation));
		}
		return relation;
	}

	@Override
	public DataFilter createFilter(Object entity) throws Exception {
		return this.createFilter(entity, null, this.direction, new HashMap<>());
	}

	public IDataRelation createFilter(Object entity, IDataNode caller, Direction direction,
			Map<Object, DataFilter> includedData) throws Exception {
		if (includedData.containsKey(entity))
			return (IDataRelation) includedData.get(entity);

		IDataRelation relation = null;
		if (this.isIdSet(entity)) {
			// update (id)
			relation = this.storage.getFactory().createIdDataRelation(this.direction, this.getId(entity), entity);
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
			caller = _getDataNode(this.startField, entity, includedData, relation);
			relation.setStart(caller);

			if (this.stEqTr)
				relation.setTarget(caller);
			else
				relation.setTarget(_getDataNode(this.targetField, entity, includedData, relation));

			return relation;
		}

		if (this.stEqTr) {
			relation.setStart(caller);
			relation.setTarget(caller);
			return relation;
		}

		if ((this.direction == Direction.OUTGOING && direction == Direction.INCOMING)
				|| (this.direction == Direction.INCOMING && direction == Direction.OUTGOING)) {
			relation.setTarget(caller);
			relation.setStart(_getDataNode(this.startField, entity, includedData, relation));
		} else {
			relation.setStart(caller);
			relation.setTarget(_getDataNode(this.targetField, entity, includedData, relation));
		}
		return relation;
	}

	private IFNode _getNode(Class<?> type, IFRelation relation) {
		NodePattern node = this.storage.getNode(type);
		if (node == null)
			return null;
		return node.createFilter(relation);
	}

	private IDataNode _getDataNode(Field field, Object entity, Map<Object, DataFilter> includedData,
			IDataRelation relation) throws Exception {
		NodePattern node = this.storage.getNode(field.getType());
		if (node == null)
			return null;
		IDataNode dataNode = node.createFilter(field.get(entity), includedData);
		dataNode.getRelations().add(relation);
		return dataNode;
	}
	
	@Override
	public Object parse(List<Data> data) throws Exception {
		Data primary = data.get(0);
		Object relation = this.getBuffer().acquire(primary.getId(), this.type, this.parse(primary.getId(), primary.getData()));
		
		
		
		
		
		
		
		
		
		// TODO Auto-generated method stub
		return relation;
	}

}
