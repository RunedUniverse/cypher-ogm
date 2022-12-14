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
package net.runeduniverse.lib.rogm.api.pattern;

import java.io.Serializable;

import net.runeduniverse.lib.rogm.api.annotations.IConverter;
import net.runeduniverse.lib.rogm.api.buffer.IBuffer;
import net.runeduniverse.lib.rogm.api.container.IDeleteContainer;
import net.runeduniverse.lib.rogm.api.container.ISaveContainer;
import net.runeduniverse.lib.rogm.api.container.IUpdatedEntryContainer;
import net.runeduniverse.lib.rogm.api.querying.IFilter;
import net.runeduniverse.lib.rogm.api.querying.IQueryBuilderInstance;

public interface IBaseQueryPattern<B extends IQueryBuilderInstance<?, ?, ? extends IFilter>> extends IPattern {

	boolean isIdSet(Object entity);

	Serializable getId(Object entity);

	IConverter<?> getIdConverter();

	Object setId(Object entity, Serializable id) throws IllegalArgumentException;

	Serializable prepareEntityId(Serializable id, Serializable entityId);

	void prepareEntityId(IData data);

	Object prepareEntityUpdate(final IBuffer buffer, IData data);

	B search(boolean lazy) throws Exception;

	// search exactly 1 node / query deeper layers for node
	B search(Serializable id, boolean lazy) throws Exception;

	B completeSearch(B builder) throws Exception;

	ISaveContainer save(Object entity, Integer depth) throws Exception;

	IDeleteContainer delete(final Serializable id, Object entity) throws Exception;

	default void prepareEntityId(final IUpdatedEntryContainer container) {
		container.setEntityId(this.prepareEntityId(container.getId(), container.getEntityId()));
	}
}
