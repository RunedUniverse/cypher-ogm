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
package net.runeduniverse.lib.rogm.api.querying;

import java.io.Serializable;

import lombok.Getter;
import net.runeduniverse.lib.rogm.api.annotations.Direction;

@Getter
public class FilterRelation extends AParamFilter<FilterRelation> implements IFRelation, IIdentified<Serializable> {

	private IFNode start = null;
	private IFNode target = null;
	private Direction direction;
	private Serializable id;

	public FilterRelation() {
		super.setInstance(this);
		this.direction = Direction.BIDIRECTIONAL;
	}

	public FilterRelation(Direction direction) {
		super.setInstance(this);
		this.direction = direction;
	}

	public FilterRelation(IFNode start, IFNode target, Direction direction) {
		super.setInstance(this);
		this.start = start;
		this.target = target;
		this.direction = direction;
	}

	public FilterRelation(Serializable id) {
		super.setInstance(this);
		this.id = id;
		this.direction = Direction.BIDIRECTIONAL;
	}

	public FilterRelation(Serializable id, Direction direction) {
		super.setInstance(this);
		this.id = id;
		this.direction = direction;
	}

	public FilterRelation(Serializable id, IFNode start, IFNode target, Direction direction) {
		super.setInstance(this);
		this.id = id;
		this.start = start;
		this.target = target;
		this.direction = direction;
	}

	public FilterRelation setDirection(Direction direction) {
		this.direction = direction;
		return this;
	}

	public FilterRelation setStart(IFNode node) {
		this.start = node;
		return this;
	}

	public FilterRelation setTarget(IFNode node) {
		this.target = node;
		return this;
	}

}
