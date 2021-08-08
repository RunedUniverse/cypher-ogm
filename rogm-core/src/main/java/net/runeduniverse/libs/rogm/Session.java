package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.Collection;

import net.runeduniverse.libs.rogm.pipeline.Pipeline;
import net.runeduniverse.libs.rogm.pipeline.transaction.DatabaseTransactionFactory;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;

public interface Session extends AutoCloseable {

	boolean isConnected();

	<T, ID extends Serializable> T load(Class<T> type, ID id);

	<T, ID extends Serializable> T load(Class<T> type, ID id, Integer depth);

	<T, ID extends Serializable> T loadLazy(Class<T> type, ID id);

	@Deprecated
	<T, ID extends Serializable> T load(Class<T> type, IFilter filter);

	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id);

	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id, Integer depth);

	<T, ID extends Serializable> Collection<T> loadAllLazy(Class<T> type, ID id);

	<T> Collection<T> loadAll(Class<T> type);

	<T> Collection<T> loadAll(Class<T> type, Integer depth);

	<T> Collection<T> loadAllLazy(Class<T> type);

	@Deprecated
	<T> Collection<T> loadAll(Class<T> type, IFilter filter);

	void resolveLazyLoaded(Object entity);

	void resolveLazyLoaded(Object entity, Integer depth);

	void resolveAllLazyLoaded(Collection<? extends Object> entities);

	void resolveAllLazyLoaded(Collection<? extends Object> entities, Integer depth);

	void reload(Object entity);

	void reload(Object entity, Integer depth);

	void reloadAll(Collection<? extends Object> entities);

	void reloadAll(Collection<? extends Object> entities, Integer depth);

	void save(Object entity);

	void save(Object entity, Integer depth);

	void saveLazy(Object entity);

	void saveAll(Collection<? extends Object> entities);

	void saveAll(Collection<? extends Object> entities, Integer depth);

	void saveAllLazy(Collection<? extends Object> entities);

	void delete(Object entity);

	void deleteAll(Collection<? extends Object> entities);

	void unload(Object entity);

	void unloadAll(Collection<? extends Object> entities);

	QueryBuilder getQueryBuilder();

	public static Session create(Configuration cnf) throws Exception {
		return new Pipeline(new DatabaseTransactionFactory(cnf)).buildSession();
	}
}
