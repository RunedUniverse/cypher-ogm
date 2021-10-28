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
package net.runeduniverse.libs.rogm.buffer;

import java.io.Serializable;
import java.util.Collection;

import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pipeline.chain.data.UpdatedEntryContainer;;

public interface IBuffer extends InternalBufferTypes {

	/***
	 * Load Entity defined by Id. The Id gets defined from the Database.
	 * 
	 * @param <Serializable> id
	 * @param <Class>        type of <T>
	 * @return the <Object> of the requested class <T>
	 */
	<T> T getById(Serializable id, Class<T> type);

	/***
	 * Load Entity defined by entityId, in some cases it matches the entityId. The
	 * Id can be defined in the Object.
	 * 
	 * @param <Serializable> id
	 * @param <Class>        type of <T>
	 * @return the <Object> of the requested class <T>
	 */
	<T> T getByEntityId(Serializable entityId, Class<T> type);

	<T> T getCompleteByEntityId(Serializable entityId, Class<T> type);

	void addEntry(Entry entry);

	void addEntry(Serializable id, Serializable entityId, Object entity, LoadState loadState,
			IBaseQueryPattern<?> pattern);

	void updateEntry(Archive archive, UpdatedEntryContainer container) throws Exception;

	void removeEntry(Entry entry);

	void removeEntry(Object entity);

	void eraseRelations(Serializable deletedId, Serializable relationId, Serializable nodeId);

	Entry getEntry(Object entity);

	Collection<Entry> getAllEntries();

	// INTERNAL USE //
	@Deprecated
	void updateEntry(Entry entry, Serializable id, Serializable entityId);

	@Deprecated
	TypeEntry getTypeEntry(Class<?> type);

}
