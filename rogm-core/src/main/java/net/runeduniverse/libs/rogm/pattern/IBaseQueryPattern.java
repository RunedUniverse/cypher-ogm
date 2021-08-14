package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;

public interface IBaseQueryPattern extends IPattern {
	IQueryBuilder<?, ? extends IFilter> search(boolean lazy) throws Exception;

	// search exactly 1 node / querry deeper layers for node
	IQueryBuilder<?, ? extends IFilter> search(Serializable id, boolean lazy) throws Exception;

	ISaveContainer save(Object entity, Integer depth) throws Exception;

	IDeleteContainer delete(final IBuffer buffer, Object entity) throws Exception;
}
