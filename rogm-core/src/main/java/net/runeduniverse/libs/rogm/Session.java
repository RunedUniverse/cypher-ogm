package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.Collection;

import net.runeduniverse.libs.rogm.pipeline.DatabasePipelineFactory;
import net.runeduniverse.libs.rogm.pipeline.Pipeline;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;

/**
 * The Session Interface provides access to the connected database.
 * <p>
 * A Session can be created either by invoking the default
 * {@link DatabasePipelineFactory} through {@link Session#create(Configuration)}
 * or by initializeing it through a {@link Pipeline}.
 * <p>
 * In any case it may require you to provide you a {@link Configuration}, it is
 * recommended to use the Configuration provided with the Database-Module you
 * wish to use (usually prefixed with the Database-Name).
 *
 * @author Pl4yingNight
 */
public interface Session extends AutoCloseable {

	/**
	 * Provides information of the connection-status.
	 * 
	 * @return status of the database connection
	 */
	boolean isConnected();

	/**
	 * Loads the Object of Class<{@link T}> matching the provided id. In case the
	 * Object is still buffered the reference will be returned.
	 * 
	 * @param <T>  Model-Class provided through the Config
	 * @param <ID> {@link Serializable} Object
	 * @param type requested Type
	 * @param id   requested entity-id
	 * @return null, new or buffered Object of the defined type
	 */
	<T, ID extends Serializable> T load(Class<T> type, ID id);

	/**
	 * Loads the Object of Class<{@link T}> matching the provided id. In case the
	 * Object is still buffered the reference will be returned.
	 * <p>
	 * Depth defines how often the requested Objects Relations will be loaded
	 * recursively. <code>0</code> will load the Object without any Relations
	 * (LAZY).
	 * 
	 * @param <T>   Model-Class provided through the Config
	 * @param <ID>  {@link Serializable} Object
	 * @param type  requested Type
	 * @param id    requested entity-id
	 * @param depth requested load depth
	 * @return null, new or buffered Object of the defined type
	 */
	<T, ID extends Serializable> T load(Class<T> type, ID id, Integer depth);

	/**
	 * Lazy-Loads the Object of Class<{@link T}> matching the provided id. Except
	 * the Object is still buffered the buffered reference (LAZY or not) will be
	 * returned.
	 * 
	 * @see Session#load(Class, Serializable, Integer)
	 * 
	 * @param <T>  Model-Class provided through the Config
	 * @param <ID> {@link Serializable} Object
	 * @param type requested Type
	 * @param id   requested entity-id
	 * @return null, new or buffered Object of the defined type
	 */
	<T, ID extends Serializable> T loadLazy(Class<T> type, ID id);

	/**
	 * Loads the first Object defined through the {@link IFilter} Object.
	 * <p>
	 * In case of use it is recommended to build the Filter with the
	 * {@link QueryBuilder} acquirable through {@link Session#getQueryBuilder()}.
	 * 
	 * @deprecated because it may not return the Object of the correct Class
	 * @param <T>    Model-Class provided through the Config
	 * @param filter Custom-Filter
	 * @return null, new or buffered Object defined through the {@link IFilter}
	 *         Object
	 */
	@Deprecated
	<T, ID extends Serializable> T load(IFilter filter);

	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id);

	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id, Integer depth);

	<T, ID extends Serializable> Collection<T> loadAllLazy(Class<T> type, ID id);

	<T> Collection<T> loadAll(Class<T> type);

	<T> Collection<T> loadAll(Class<T> type, Integer depth);

	<T> Collection<T> loadAllLazy(Class<T> type);

	@Deprecated
	<T> Collection<T> loadAll(IFilter filter);

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

	@SuppressWarnings("resource")
	public static Session create(Configuration cnf) throws Exception {
		return new Pipeline(new DatabasePipelineFactory(cnf)).buildSession();
	}
}
