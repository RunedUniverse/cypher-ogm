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
package net.runeduniverse.lib.rogm.api.buffer;

import net.runeduniverse.lib.rogm.api.container.ILazyEntriesContainer;
import net.runeduniverse.lib.rogm.api.querying.IFilter;
import net.runeduniverse.lib.rogm.api.querying.ILazyLoading;

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

	public static Object merge(IEntry entry, LoadState state, ILazyEntriesContainer lazyEntries) {
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