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
package net.runeduniverse.lib.rogm.test.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.lib.rogm.annotations.Relationship;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Artist extends Person {

	@Relationship(label = "CREATED")
	private Set<Song> created = new HashSet<>();

	@Relationship(label = "SINGS")
	private Set<Song> sang = new HashSet<>();

	@Relationship(label = "PLAYS")
	private Set<Song> played = new HashSet<>();

}