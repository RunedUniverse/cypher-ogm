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
package net.runeduniverse.lib.rogm.test.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.lib.rogm.annotations.Converter;
import net.runeduniverse.lib.rogm.annotations.Direction;
import net.runeduniverse.lib.rogm.annotations.Id;
import net.runeduniverse.lib.rogm.annotations.NodeEntity;
import net.runeduniverse.lib.rogm.annotations.PostDelete;
import net.runeduniverse.lib.rogm.annotations.PostLoad;
import net.runeduniverse.lib.rogm.annotations.PostSave;
import net.runeduniverse.lib.rogm.annotations.PreDelete;
import net.runeduniverse.lib.rogm.annotations.PreSave;
import net.runeduniverse.lib.rogm.annotations.Relationship;
import net.runeduniverse.lib.rogm.annotations.IConverter.UUIDConverter;

@Getter
@NodeEntity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Player {

	@Id
	@Converter(converter = UUIDConverter.class)
	@Setter
	private UUID uuid;

	@Setter
	private String name;
	
	@Setter
	@Relationship(label = "PLAYER_INV", direction = Direction.OUTGOING)
	private Inventory inventory;

	@PreSave
	private void preSave() {
		System.out.println("[PRE-SAVE] " + toString());
	}

	@PostSave
	private void postSave() {
		System.out.println("[POST-SAVE] " + toString());
	}

	@PostLoad
	private void postLoad() {
		System.out.println("[POST-LOAD] " + toString());
	}

	@PreDelete
	public void preDelete() {
		System.out.println("[PRE-DELETE] " + toString());
	}

	@PostDelete
	public void postDelete() {
		System.out.println("[POST-DELETE] " + toString());
	}
}
