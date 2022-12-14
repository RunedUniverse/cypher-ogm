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
package net.runeduniverse.lib.rogm.api.querying.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.lib.rogm.api.querying.IReturned;
import net.runeduniverse.lib.utils.logging.logs.CompoundTree;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReturnedHandler implements IReturned, NoFilterType, ITraceable {

	private boolean returned = false;

	@Override
	public void toRecord(CompoundTree tree) {
		tree.append("RETURNED", this.returned ? "TRUE" : "FALSE");
	}
}
