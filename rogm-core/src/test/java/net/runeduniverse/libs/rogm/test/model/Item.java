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
package net.runeduniverse.libs.rogm.test.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.PreSave;
import net.runeduniverse.libs.rogm.annotations.Property;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.annotations.Transient;

@Getter
@Setter
@ToString(callSuper = true)
public class Item extends AEntity {
	@Relationship(label = "CONTAINS_INVENTORY", direction = Direction.OUTGOING)
	private Inventory containingInventory;

	private String str = "my string";
	private Boolean bool = false;
	
	@Property
	private String itemStackData = null;
	
	@Transient
	@Getter
	private String itemStack = null;
	
	@PreSave
	private void _preSave() {
		this.itemStackData = "{type:"+itemStack+'}';
	}
}
