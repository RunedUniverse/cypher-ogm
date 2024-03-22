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
package net.runeduniverse.lib.rogm.buffer;

import java.io.Serializable;

import net.runeduniverse.lib.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.lib.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.rogm.querying.ILazyLoading;

public interface BufferTypes {

	public interface IEntry {
		Serializable getId();

		Serializable getEntityId();

		Object getEntity();

		LoadState getLoadState();

		Class<?> getType();

		IBaseQueryPattern<?> getPattern();

		void setId(Serializable id);

		void setEntityId(Serializable entityId);

		void setEntity(Object entity);

		void setLoadState(LoadState state);

		void setType(Class<?> type);

		void setPattern(IBaseQueryPattern<?> pattern);
	}

	public enum LoadState {
		COMPLETE, LAZY;

		public static LoadState get(boolean lazy) {
			if (lazy)
				return LAZY;
			return COMPLETE;
		}

		public static LoadState get(IFilter filter) {
			return get(filter instanceof ILazyLoading && ((ILazyLoading) filter).isLazy());
		}

		public static Object merge(IEntry entry, LoadState state, LazyEntriesContainer lazyEntries) {
			if (entry.getLoadState() == COMPLETE || state == COMPLETE)
				entry.setLoadState(COMPLETE);
			else {
				entry.setLoadState(LAZY);
				if (lazyEntries != null)
					lazyEntries.addEntry(entry);
			}
			return entry.getEntity();
		}
	}
}
