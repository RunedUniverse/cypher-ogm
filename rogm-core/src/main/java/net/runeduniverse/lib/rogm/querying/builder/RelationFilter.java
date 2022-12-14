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
package net.runeduniverse.lib.rogm.querying.builder;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.lib.rogm.api.annotations.Direction;
import net.runeduniverse.lib.rogm.api.querying.IFNode;
import net.runeduniverse.lib.rogm.api.querying.IFRelation;

@Getter
@Setter
public class RelationFilter extends AProxyFilter<RelationFilter> implements IFRelation {

	private Direction direction;
	private IFNode start;
	private IFNode target;

	public RelationFilter() {
		this.instance = this;
	}

}
