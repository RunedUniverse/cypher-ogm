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
package net.runeduniverse.libs.rogm.pipeline.chain.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.Entry;

@NoArgsConstructor
@ToString
public class LazyEntriesContainer {
	@Getter
	private final Set<Entry> lazyEntries = new HashSet<>();

	public boolean addEntry(Entry entry) {
		return this.lazyEntries.add(entry);
	}

	public boolean addAllEntries(Collection<Entry> entries) {
		return this.lazyEntries.addAll(entries);
	}

	public boolean addEntries(LazyEntriesContainer container) {
		return this.lazyEntries.addAll(container.getLazyEntries());
	}

	public boolean isEmpty() {
		return this.lazyEntries.isEmpty();
	}

	public void clear() {
		this.lazyEntries.clear();
	}
}
