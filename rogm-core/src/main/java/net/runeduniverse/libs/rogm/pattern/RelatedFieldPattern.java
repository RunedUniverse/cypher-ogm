package net.runeduniverse.libs.rogm.pattern;

import static net.runeduniverse.libs.utils.StringUtils.isBlank;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataNode;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.IDataRelation;
import net.runeduniverse.libs.rogm.pattern.FilterFactory.Relation;
import net.runeduniverse.libs.rogm.querying.FilterType;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;

@Getter
@Setter
public class RelatedFieldPattern extends FieldPattern implements IValidatable {

	private String label = null;
	private Direction direction;
	private boolean definedRelation;

	public RelatedFieldPattern(IStorage factory, Field field) throws Exception {
		super(factory, field);
	}

	@Override
	public void validate() throws Exception {
		Relationship fieldAnno = this.field.getAnnotation(Relationship.class);
		this.direction = fieldAnno.direction();

		if (this.type.isAnnotationPresent(NodeEntity.class))
			this.definedRelation = false;
		else if (this.type.isAnnotationPresent(RelationshipEntity.class)) {
			this.label = this.factory.getRelation(this.type)
					.getLabel();
			this.definedRelation = true;
		} else
			throw new Exception("Unsupported Class<" + this.type.getName() + "> as @Relation found!");
		if (isBlank(this.label))
			this.label = isBlank(fieldAnno.label()) ? this.field.getName() : fieldAnno.label();
	}

	public IFRelation queryRelation(IFNode origin) throws Exception {
		Relation relation = null;
		if (this.definedRelation)
			relation = this.factory.getRelation(this.type)
					.createFilter(origin, this.direction);
		else {
			relation = this.factory.getFactory()
					.createRelation(this.direction);
			relation.setStart(origin);
			relation.setTarget(this._getNode(this.type, relation));
		}

		if (relation.getLabels()
				.isEmpty())
			relation.getLabels()
					.add(this.label);

		relation.setReturned(true);
		relation.setOptional(true);
		return relation;
	}

	private IFNode _getNode(Class<?> type, IFRelation relation) throws Exception {
		INodePattern node = this.factory.getNode(type);
		if (node == null)
			throw new Exception("Unsupported Class<" + type.getName() + "> as @Relation found!");
		return node.search(relation, true);
	}

	public void saveRelation(Object entity, IDataNode node, Map<Object, IDataContainer> includedData, Integer depth)
			throws Exception {
		if (entity == null)
			return;
		if (this.collection)
			// Collection
			for (Object relNode : (Collection<?>) this.field.get(entity))
				_addRelation(node, relNode, includedData, depth);
		else
			// Variable
			_addRelation(node, this.field.get(entity), includedData, depth);
	}

	private void _addRelation(IDataNode node, Object relEntity, Map<Object, IDataContainer> includedData, Integer depth)
			throws Exception {
		if (relEntity == null)
			return;

		IDataRelation relation = null;
		// clazz could be substituted with this.type but isn't in case the entities type
		// is a child of this.type
		Class<?> clazz = relEntity.getClass();
		if (clazz.isAnnotationPresent(RelationshipEntity.class)) {
			relation = this.factory.getRelation(clazz)
					.save(relEntity, node, this.direction, includedData, depth);
			if (relation == null)
				return;
		} else {
			relation = this.factory.getFactory()
					.createDataRelation(this.direction, null);
			relation.setFilterType(FilterType.UPDATE);
			relation.setStart(node);
			relation.setTarget(this.factory.getNode(clazz)
					.save(relEntity, includedData, depth));
		}

		if (relation.getLabels()
				.isEmpty())
			relation.getLabels()
					.add(this.label);

		node.getRelations()
				.add(relation);
	}

}
