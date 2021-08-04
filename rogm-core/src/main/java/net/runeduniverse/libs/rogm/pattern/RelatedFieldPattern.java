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
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.RelationQueryBuilder;

@Getter
@Setter
public class RelatedFieldPattern extends FieldPattern implements IValidatable {

	private String label = null;
	private Direction direction;
	private boolean definedRelation;

	public RelatedFieldPattern(Archive archive, Field field) throws Exception {
		super(archive, field);
	}

	@Override
	public void validate() throws Exception {
		Relationship fieldAnno = this.field.getAnnotation(Relationship.class);
		this.direction = fieldAnno.direction();

		if (this.type.isAnnotationPresent(NodeEntity.class))
			this.definedRelation = false;
		else if (this.type.isAnnotationPresent(RelationshipEntity.class)) {
			this.label = this.archive.getPattern(this.type, RelationPattern.class)
					.getLabel();
			this.definedRelation = true;
		} else
			throw new Exception("Unsupported Class<" + this.type.getName() + "> as @Relation found!");
		if (isBlank(this.label))
			this.label = isBlank(fieldAnno.label()) ? this.field.getName() : fieldAnno.label();
	}

	public RelationQueryBuilder queryRelation(NodeQueryBuilder origin) throws Exception {
		RelationQueryBuilder relationBuilder = null;
		if (this.definedRelation)
			relationBuilder = this.archive.getPattern(this.type, RelationPattern.class)
					.createFilter(origin, direction);
		else {
			relationBuilder = this.archive.getQueryBuilder()
					.relation()
					.whereDirection(this.direction);
			relationBuilder.setStart(origin);
			relationBuilder.setTarget(this._getNode(this.type, relationBuilder));
		}

		if (relationBuilder.getLabels()
				.isEmpty())
			relationBuilder.getLabels()
					.add(this.label);

		relationBuilder.setReturned(true);
		relationBuilder.setOptional(true);
		return relationBuilder;
	}

	private NodeQueryBuilder _getNode(Class<?> type, RelationQueryBuilder relation) throws Exception {
		INodePattern node = this.archive.getPattern(type, NodePattern.class);
		if (node == null)
			throw new Exception("Unsupported Class<" + type.getName() + "> as @Relation found!");
		return node.search(relation, true);
	}

	public void saveRelation(Object entity, NodeQueryBuilder nodeBuilder,
			Map<Object, IQueryBuilder<?, ? extends IFilter>> includedData, Integer depth) throws Exception {
		if (entity == null)
			return;
		if (this.collection)
			// Collection
			for (Object relNode : (Collection<?>) this.field.get(entity))
				_addRelation(nodeBuilder, relNode, includedData, depth);
		else
			// Variable
			_addRelation(nodeBuilder, this.field.get(entity), includedData, depth);
	}

	@SuppressWarnings("deprecation")
	private void _addRelation(NodeQueryBuilder nodeBuilder, Object relEntity,
			Map<Object, IQueryBuilder<?, ? extends IFilter>> includedData, Integer depth) throws Exception {
		if (relEntity == null)
			return;

		RelationQueryBuilder relationBuilder = null;
		// clazz could be substituted with this.type but isn't in case the entities type
		// is a child of this.type
		Class<?> clazz = relEntity.getClass();
		if (clazz.isAnnotationPresent(RelationshipEntity.class)) {
			relationBuilder = this.archive.getPattern(clazz, RelationPattern.class)
					.save(relEntity, nodeBuilder, this.direction, includedData, depth);
			if (relationBuilder == null)
				return;
		} else {
			relationBuilder = this.archive.getQueryBuilder()
					.relation()
					.whereDirection(this.direction)
					.setStart(nodeBuilder)
					.setTarget(this.archive.getPattern(clazz, NodePattern.class)
							.save(relEntity, includedData, depth));
			// relationBuilder =
			// this.factory.getFactory().createDataRelation(this.direction, null);
			// relationBuilder.setFilterType(FilterType.UPDATE);
			// relationBuilder.setStart(node);
			// relationBuilder.setTarget(this.factory.getNode(clazz).save(relEntity,
			// includedData, depth));
		}

		if (relationBuilder.getLabels()
				.isEmpty())
			relationBuilder.getLabels()
					.add(this.label);

		nodeBuilder.addRelation(relationBuilder);
	}

}
