/*
 * Copyright © 2023 VenaNocta (venanocta@gmail.com)
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

import java.util.Map;

import net.runeduniverse.lib.rogm.annotations.Direction;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.rogm.querying.IQueryBuilder;
import net.runeduniverse.lib.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.lib.rogm.querying.QueryBuilder.RelationQueryBuilder;

public interface IRelationPattern<B extends RelationQueryBuilder> extends IBaseQueryPattern<B>, IValidatable {
	String getLabel();

	RelationQueryBuilder createFilter(NodeQueryBuilder caller, Direction direction);

	RelationQueryBuilder save(Object entity, NodeQueryBuilder caller, Direction direction,
			Map<Object, IQueryBuilder<?, ?, ? extends IFilter>> includedData, Integer depth) throws Exception;
}
