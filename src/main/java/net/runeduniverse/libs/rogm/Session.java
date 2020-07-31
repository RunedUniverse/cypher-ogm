package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.Collection;

import net.runeduniverse.libs.rogm.querying.IFilter;

public interface Session extends AutoCloseable {

	boolean isConnected();

	<T, ID extends Serializable> T load(Class<T> type, ID id);

	<T, ID extends Serializable> T load(Class<T> type, ID id, Integer depth);

	<T, ID extends Serializable> T loadLazy(Class<T> type, ID id);

	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id);

	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id, Integer depth);

	<T, ID extends Serializable> Collection<T> loadAllLazy(Class<T> type, ID id);

	<T> Collection<T> loadAll(Class<T> type);

	<T> Collection<T> loadAll(Class<T> type, Integer depth);

	<T> Collection<T> loadAllLazy(Class<T> type);

	<T> Collection<T> loadAll(Class<T> type, IFilter filter);

	void resolveLazyLoaded(Object entity);

	void resolveAllLazyLoaded(Collection<Object> entities);

	void save(Object entity);

	void save(Object entity, Integer depth);

	void saveLazy(Object entity);

	void saveAll(Collection<Object> entities);

	void saveAll(Collection<Object> entities, Integer depth);

	void saveAllLazy(Collection<Object> entities);

	void delete(Object entity);

	void deleteAll(Collection<Object> entities);

	public static Session create(Configuration cnf) throws Exception {
		return new CoreSession(cnf);
	}
}
