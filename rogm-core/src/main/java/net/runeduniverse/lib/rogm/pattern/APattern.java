/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runeduniverse.lib.rogm.annotations.IConverter;
import net.runeduniverse.lib.rogm.annotations.Id;
import net.runeduniverse.lib.rogm.annotations.PreReload;
import net.runeduniverse.lib.rogm.buffer.IBuffer;
import net.runeduniverse.lib.rogm.querying.IFRelation;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.rogm.querying.IQueryBuilder;
import net.runeduniverse.lib.utils.scanner.pattern.DefaultTypePattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.MethodPattern;

public abstract class APattern<B extends IQueryBuilder<?, ?, ? extends IFilter>>
		extends DefaultTypePattern<FieldPattern, MethodPattern> implements IBaseQueryPattern<B>, IValidatable {

	protected final Archive archive;
	protected FieldPattern idFieldPattern;
	@Getter
	protected IConverter<?> idConverter = null;
	@Getter
	private boolean valid = false;

	public APattern(Archive archive, String pkg, ClassLoader loader, Class<?> type) {
		super(pkg, loader, type);
		this.archive = archive;
	}

	public void validate() throws Exception {
		this.idFieldPattern = super.getField(Id.class);
		if (this.idFieldPattern != null)
			this.idConverter = this.idFieldPattern.getConverter();
		for (FieldPattern pattern : getFields())
			IValidatable.validate(pattern);
		this.valid = true;
	}

	@Override
	public boolean isIdSet(Object entity) {
		return this.getId(entity) != null;
	}

	@Override
	public Serializable getId(Object entity) {
		if (this.idFieldPattern == null)
			return null;
		return (Serializable) this.idFieldPattern.getValue(entity);
	}

	@Override
	public Object setId(Object entity, Serializable id) {
		if (this.idFieldPattern != null)
			this.idFieldPattern.setValue(entity, id);
		return entity;
	}

	@Override
	public Serializable prepareEntityId(Serializable id, Serializable entityId) {
		if (this.idFieldPattern == null || entityId == null)
			return id;
		else if (entityId instanceof String)
			return this.idFieldPattern.getConverter()
					.convert((String) entityId);
		return entityId;
	}

	@Override
	public void prepareEntityId(IData data) {
		if (this.idFieldPattern != null)
			data.setEntityId(prepareEntityId(data.getId(), data.getEntityId()));
	}

	@Override
	public Object prepareEntityUpdate(final IBuffer buffer, IData data) {
		this.prepareEntityId(data);

		Object entity = buffer.getById(data.getId(), this.type);
		this.callMethod(PreReload.class, entity);
		return entity;
	}

	@RequiredArgsConstructor
	@Getter
	protected class DeleteContainer implements IDeleteContainer {
		private final IPattern pattern;
		private final Object entity;
		private final Serializable deletedId;
		private final IFRelation effectedFilter;
		private final IFilter deleteFilter;
	}
}
