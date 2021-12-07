/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.lib.rogm.querying;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.lib.rogm.annotations.Direction;

public class FilterNode extends AParamFilter<FilterNode> implements IFNode, IIdentified<Serializable> {

	@Getter
	Set<IFRelation> relations = new HashSet<>();
	@Getter
	private Serializable id;

	public FilterNode() {
		super.setInstance(this);
	}

	public FilterNode(Serializable id) {
		this.id = id;
		super.setInstance(this);
	}

	// Relations
	public FilterNode addRelation(IFRelation relation) {
		relations.add(relation);
		return this;
	}

	public FilterNode addRelation(FilterRelation relation, IFNode target) {
		relations.add(relation.setStart(this).setTarget(target));
		return this;
	}

	// Relations TO
	public FilterNode addRelationTo(IFNode node) {
		relations.add(new FilterRelation(this, node, Direction.OUTGOING));
		return this;
	}

	public FilterNode addRelationTo(FilterRelation relation) {
		relations.add(relation.setStart(this).setDirection(Direction.OUTGOING));
		return this;
	}

	// Relations From
	public FilterNode addRelationFrom(IFNode node) {
		relations.add(new FilterRelation(node, this, Direction.INCOMING));
		return this;
	}

	public FilterNode addRelationFrom(FilterRelation relation) {
		relations.add(relation.setTarget(this).setDirection(Direction.INCOMING));
		return this;
	}
}
