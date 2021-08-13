package net.runeduniverse.libs.rogm.pattern;

import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;

public interface IQueryPattern extends IPattern{
	void search(IQueryBuilder<?, ? extends IFilter> builder) throws Exception;

	void save(ISaveContainer container) throws Exception;

	void delete(IDeleteContainer container) throws Exception;
}
