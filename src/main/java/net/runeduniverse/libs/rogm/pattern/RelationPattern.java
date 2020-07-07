package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;

import static net.runeduniverse.libs.rogm.util.Utils.isBlank;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.EndNode;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.annotations.StartNode;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Relation;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class RelationPattern implements IPattern {

	private final PatternStorage storage;
	private final Class<?> type;

	private String label = null;
	private Direction direction = null;
	private Field idField = null;
	private Field startField = null;
	private Field targetField = null;
	// start eq target
	@Getter
	private boolean stEqTr = false;

	public RelationPattern(PatternStorage storage, Class<?> type) {
		this.storage = storage;
		this.type = type;
		RelationshipEntity typeAnno = this.type.getAnnotation(RelationshipEntity.class);
		this.direction = typeAnno.direction();
		this.label = typeAnno.label();
		_parse(this.type);
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
		Relation relation = this.storage.getFactory().createRelation(this.direction);
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
		return relation;
	}

	public IFRelation createFilter(IFNode caller, Direction direction) {
		Relation relation = this.storage.getFactory().createRelation(this.direction);
		if (isBlank(this.label))
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

	private IFNode _getNode(Class<?> type, IFRelation relation) {
		NodePattern node = this.storage.getNode(type);
		if (node == null)
			return null;
		return node.createFilter(relation);
	}

	public Object setId(Object object, Serializable id) throws IllegalArgumentException {
		if (this.idField == null)
			return object;
		try {
			this.idField.set(object, id);
		} catch (IllegalAccessException e) {
		}
		return object;
	}

	public Object parse(Serializable id, String data) throws Exception {
		return this.setId(this.storage.getParser().deserialize(this.type, data), id);
	}

}
