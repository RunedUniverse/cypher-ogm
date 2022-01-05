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
package net.runeduniverse.lib.rogm.buffer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.runeduniverse.lib.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.lib.rogm.pattern.IPattern.IData;

public interface InternalBufferTypes extends BufferTypes {

	@NoArgsConstructor
	public class TypeEntry {

		protected Map<Serializable, Entry> idMap = new HashMap<>();
		protected Map<Serializable, Entry> entityIdMap = new HashMap<>();

		public Entry getIdEntry(Serializable id) {
			return this.idMap.get(id);
		}

		public Entry getEntityIdEntry(Serializable id) {
			return this.entityIdMap.get(id);
		}
	}

	@Data
	@AllArgsConstructor
	public class Entry implements IEntry {

		private Serializable id;
		private Serializable entityId;
		private Object entity;
		private LoadState loadState;
		private Class<?> type;
		private IBaseQueryPattern<?> pattern;

		public Entry(IData data, Object entity, LoadState loadState, IBaseQueryPattern<?> pattern) {
			this.id = data.getId();
			this.entityId = data.getEntityId();
			this.entity = entity;
			this.loadState = loadState;
			this.type = entity.getClass();
			this.pattern = pattern;
		}

		public static Entry from(IEntry iEntry) {
			if (iEntry instanceof Entry)
				return (Entry) iEntry;
			return new Entry(iEntry.getId(), iEntry.getEntityId(), iEntry.getEntity(), iEntry.getLoadState(),
					iEntry.getType(), iEntry.getPattern());
		}
	}
}
