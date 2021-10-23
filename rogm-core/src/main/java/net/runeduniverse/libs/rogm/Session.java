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
	 * Object is still buffered the reference will be returned. Returns
	 * <code>null</code> in case no Object could be loaded.
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
	 * Object is still buffered the reference will be returned. Returns
	 * <code>null</code> in case no Object could be loaded.
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
	 * returned. Returns <code>null</code> in case no Object could be loaded.
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
	 * Loads the first Object defined through the {@link IFilter} Object. Returns
	 * <code>null</code> in case the Filter had no loadedable results.
	 * <p>
	 * In case of use it is recommended to build the Filter with the
	 * {@link QueryBuilder} acquirable through {@link Session#getQueryBuilder()}.
	 * 
	 * @deprecated may not return the Object of the correct Class
	 * @param <T>    Model-Class provided through the Config
	 * @param filter Custom-Filter
	 * @return null, new or buffered Object defined through the {@link IFilter}
	 *         Object
	 */
	<T> T load(IFilter filter);

	/**
	 * Loads all Objects of Class<{@link T}> matching the provided id. In case an
	 * Object is still buffered the reference will be returned. Returns an empty
	 * Collection in case no Objects could be loaded.
	 * 
	 * @param <T>  Model-Class provided through the Config
	 * @param <ID> {@link Serializable} Object
	 * @param type requested Type
	 * @param id   requested entity-id
	 * @return Collection with none, new or buffered Objects of the defined type
	 */
	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id);

	/**
	 * Loads all Objects of Class<{@link T}> matching the provided id. In case an
	 * Object is still buffered the reference will be returned. Returns an empty
	 * Collection in case no Objects could be loaded.
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
	 * @return Collection with none, new or buffered Objects of the defined type
	 */
	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id, Integer depth);

	/**
	 * Lazy-Loads all Objects of Class<{@link T}> matching the provided id. Except
	 * the Object is still buffered the buffered reference (LAZY or not) will be
	 * returned. Returns an empty Collection in case no Objects could be loaded.
	 * 
	 * @see Session#loadAll(Class, Serializable, Integer)
	 * 
	 * @param <T>  Model-Class provided through the Config
	 * @param <ID> {@link Serializable} Object
	 * @param type requested Type
	 * @param id   requested entity-id
	 * @return Collection with none, new or buffered Objects of the defined type
	 */
	<T, ID extends Serializable> Collection<T> loadAllLazy(Class<T> type, ID id);

	/**
	 * Loads all Objects of Class<{@link T}>. In case an Object is still buffered
	 * the reference will be returned. Returns an empty Collection in case no
	 * Objects could be loaded.
	 * 
	 * @deprecated discouraged from using - needlessly fills up RAM
	 * @param <T>  Model-Class provided through the Config
	 * @param type requested Type
	 * @return Collection with none, new or buffered Objects of the defined type
	 */
	<T> Collection<T> loadAll(Class<T> type);

	/**
	 * Loads all Objects of Class<{@link T}>. In case an Object is still buffered
	 * the reference will be returned. Returns an empty Collection in case no
	 * Objects could be loaded.
	 * <p>
	 * Depth defines how often the requested Objects Relations will be loaded
	 * recursively. <code>0</code> will load the Object without any Relations
	 * (LAZY).
	 * 
	 * @deprecated discouraged from using - needlessly fills up RAM
	 * @param <T>   Model-Class provided through the Config
	 * @param type  requested Type
	 * @param depth requested load depth
	 * @return Collection with none, new or buffered Objects of the defined type
	 */
	<T> Collection<T> loadAll(Class<T> type, Integer depth);

	/**
	 * Lazy-Loads all Objects of Class<{@link T}>. Except the Object is still
	 * buffered the buffered reference (LAZY or not) will be returned. Returns an
	 * empty Collection in case no Objects could be loaded.
	 * 
	 * @deprecated discouraged from using - needlessly fills up RAM
	 * @see Session#loadAll(Class, Integer)
	 * 
	 * @param <T>  Model-Class provided through the Config
	 * @param type requested Type
	 * @return Collection with none, new or buffered Objects of the defined type
	 */
	<T> Collection<T> loadAllLazy(Class<T> type);

	/**
	 * Loads all Objects defined through the {@link IFilter} Object. Returns an
	 * empty Collection in case no Objects could be loaded.
	 * <p>
	 * In case of use it is recommended to build the Filter with the
	 * {@link QueryBuilder} acquirable through {@link Session#getQueryBuilder()}.
	 * 
	 * @deprecated may not return the Object of the correct Class
	 * @param <T>    Model-Class provided through the Config
	 * @param filter Custom-Filter
	 * @return Collection with none, new or buffered Objects defined through the
	 *         {@link IFilter} Object
	 */
	<T> Collection<T> loadAll(IFilter filter);

	/**
	 * Loads all Relations provided by {@link entity}.
	 * 
	 * @param entity Object of Model-Class provided through the Config
	 */
	void resolveLazyLoaded(Object entity);

	/**
	 * Loads all Relations provided by {@link entity}.
	 * <p>
	 * Depth defines how often the requested Objects Relations will be loaded
	 * recursively. <code>0</code> will load the Object without any Relations
	 * (LAZY).
	 * 
	 * @param entity Object of Model-Class provided through the Config
	 * @param depth  requested load depth
	 */
	void resolveLazyLoaded(Object entity, Integer depth);

	/**
	 * Loads all Relations provided by values of Collection{@link entities}.
	 * 
	 * @param Collection{@link entities} with Objects of Model-Classes provided
	 *                         through the Config
	 */
	void resolveAllLazyLoaded(Collection<? extends Object> entities);

	/**
	 * Loads all Relations provided by values of Collection{@link entities}.
	 * <p>
	 * Depth defines how often the requested Objects Relations will be loaded
	 * recursively. <code>0</code> will load the Object without any Relations
	 * (LAZY).
	 * 
	 * @param Collection{@link entities} with Objects of Model-Classes provided
	 *                         through the Config
	 * @param depth            requested load depth
	 */
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
