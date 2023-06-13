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
package net.runeduniverse.lib.rogm.pattern;

import java.io.Serializable;

import net.runeduniverse.lib.rogm.annotations.IConverter;
import net.runeduniverse.lib.rogm.buffer.IBuffer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.SaveContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.UpdatedEntryContainer;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.rogm.querying.IQueryBuilder;

public interface IBaseQueryPattern<B extends IQueryBuilder<?, ?, ? extends IFilter>> extends IPattern {

	boolean isIdSet(Object entity);

	Serializable getId(Object entity);

	IConverter<?> getIdConverter();

	Object setId(Object entity, Serializable id) throws IllegalArgumentException;

	Serializable prepareEntityId(Serializable id, Serializable entityId);

	void prepareEntityId(IData data);

	Object prepareEntityUpdate(final IBuffer buffer, IData data);

	B search(boolean lazy) throws Exception;

	// search exactly 1 node / querry deeper layers for node
	B search(Serializable id, boolean lazy) throws Exception;

	B completeSearch(B builder) throws Exception;

	SaveContainer save(Object entity, Integer depth) throws Exception;

	IDeleteContainer delete(final Serializable id, Object entity) throws Exception;

	default void prepareEntityId(final UpdatedEntryContainer container) {
		container.setEntityId(this.prepareEntityId(container.getId(), container.getEntityId()));
	}
}
