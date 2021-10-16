package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;

import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.SaveContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.UpdatedEntryContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;

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
