package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;

import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;

public interface IBaseQueryPattern extends IPattern {

	boolean isIdSet(Object entity);

	Serializable getId(Object entity);

	IConverter<?> getIdConverter();

	Object setId(Object entity, Serializable id) throws IllegalArgumentException;

	Serializable prepareEntityId(Serializable id, Serializable entityId);

	void prepareEntityId(IData data);

	IQueryBuilder<?, ? extends IFilter> search(boolean lazy) throws Exception;

	// search exactly 1 node / querry deeper layers for node
	IQueryBuilder<?, ? extends IFilter> search(Serializable id, boolean lazy) throws Exception;

	ISaveContainer save(final IBuffer buffer, Object entity, Integer depth) throws Exception;

	IDeleteContainer delete(final IBuffer buffer, Object entity) throws Exception;
}
