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
import java.util.Collection;

import net.runeduniverse.lib.rogm.pattern.Archive;
import net.runeduniverse.lib.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.lib.rogm.pipeline.chain.ChainConfigurator;
import net.runeduniverse.lib.rogm.pipeline.chain.data.UpdatedEntryContainer;;

public interface IBuffer extends BufferTypes, ChainConfigurator {

	/***
	 * Load Entity defined by Id. The Id gets defined from the Database.
	 * 
	 * @param <T>  {@link Object} of type
	 * @param id   {@link Serializable}
	 * @param type of <T> {@link Class}
	 * @return the {@link Object} of the requested class
	 */
	<T> T getById(Serializable id, Class<T> type);

	/***
	 * Load Entity defined by entityId, in some cases it matches the entityId. The
	 * Id can be defined in the Object.
	 * 
	 * @param <T>      {@link Object} of type
	 * @param entityId {@link Serializable}
	 * @param type     of <T> {@link Class}
	 * @return the {@link Object} of the requested class
	 */
	<T> T getByEntityId(Serializable entityId, Class<T> type);

	<T> T getCompleteByEntityId(Serializable entityId, Class<T> type);

	void addEntry(IEntry entry);

	void addEntry(Serializable id, Serializable entityId, Object entity, LoadState loadState,
			IBaseQueryPattern<?> pattern);

	void updateEntry(Archive archive, UpdatedEntryContainer container) throws Exception;

	void removeEntry(IEntry entry);

	void removeEntry(Object entity);

	void eraseRelations(Serializable deletedId, Serializable relationId, Serializable nodeId);

	IEntry getEntry(Object entity);

	Collection<IEntry> getAllEntries();

}
