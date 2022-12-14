/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.rogm.pattern;

import static net.runeduniverse.lib.utils.common.StringUtils.isBlank;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.lib.rogm.api.annotations.Direction;
import net.runeduniverse.lib.rogm.api.annotations.NodeEntity;
import net.runeduniverse.lib.rogm.api.annotations.Relationship;
import net.runeduniverse.lib.rogm.api.annotations.RelationshipEntity;
import net.runeduniverse.lib.rogm.api.pattern.IArchive;
import net.runeduniverse.lib.rogm.api.pattern.INodePattern;
import net.runeduniverse.lib.rogm.api.pattern.IRelationPattern;
import net.runeduniverse.lib.rogm.api.pattern.IValidatable;
import net.runeduniverse.lib.rogm.api.querying.IFilter;
import net.runeduniverse.lib.rogm.api.querying.IQueryBuilderInstance;
import net.runeduniverse.lib.rogm.api.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.lib.rogm.api.querying.QueryBuilder.RelationQueryBuilder;

@Getter
@Setter
public class RelatedFieldPattern extends FieldPattern implements IValidatable {

	private String label = null;
	private Direction direction;
	private boolean definedRelation;

	public RelatedFieldPattern(IArchive archive, Field field) throws Exception {
		super(archive, field);
	}

	@Override
	public void validate() throws Exception {
		Relationship fieldAnno = this.field.getAnnotation(Relationship.class);
		this.direction = fieldAnno.direction();

		if (this.type.isAnnotationPresent(NodeEntity.class))
			this.definedRelation = false;
		else if (this.type.isAnnotationPresent(RelationshipEntity.class)) {
			this.label = this.archive.getPattern(this.type, IRelationPattern.class)
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
			relationBuilder = this.archive.getPattern(this.type, IRelationPattern.class)
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
		INodePattern<?> node = this.archive.getPattern(type, INodePattern.class);
		if (node == null)
			throw new Exception("Unsupported Class<" + type.getName() + "> as @Relation found!");
		return node.search(relation, true);
	}

	public void saveRelation(Object entity, NodeQueryBuilder nodeBuilder,
			Map<Object, IQueryBuilderInstance<?, ?, ? extends IFilter>> includedData, Integer depth) throws Exception {
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

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void _addRelation(NodeQueryBuilder nodeBuilder, Object relEntity,
			Map<Object, IQueryBuilderInstance<?, ?, ? extends IFilter>> includedData, Integer depth) throws Exception {
		if (relEntity == null)
			return;

		RelationQueryBuilder relationBuilder = null;
		// clazz could be substituted with this.type but isn't in case the entities type
		// is a child of this.type
		Class<?> clazz = relEntity.getClass();
		if (clazz.isAnnotationPresent(RelationshipEntity.class)) {
			relationBuilder = this.archive.getPattern(clazz, IRelationPattern.class)
					.save(relEntity, nodeBuilder, this.direction, includedData, depth);
			if (relationBuilder == null)
				return;
		} else {
			relationBuilder = this.archive.getQueryBuilder()
					.relation()
					.setAutoGenerated(true)
					.whereDirection(this.direction)
					.setStart(nodeBuilder)
					.setTarget(this.archive.getPattern(clazz, INodePattern.class)
							.save(relEntity, includedData, depth))
					.asUpdate();
		}

		if (relationBuilder.getLabels()
				.isEmpty())
			relationBuilder.getLabels()
					.add(this.label);

		nodeBuilder.addRelation(relationBuilder);
	}

}
