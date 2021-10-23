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
	 * Loads all Relations provided by values of Collection {@link entities}.
	 * 
	 * @param Collection {@link entities} with Objects of Model-Classes provided
	 *                   through the Config
	 */
	void resolveAllLazyLoaded(Collection<? extends Object> entities);

	/**
	 * Loads all Relations provided by values of Collection {@link entities}.
	 * <p>
	 * Depth defines how often the requested Objects Relations will be loaded
	 * recursively. <code>0</code> will load the Object without any Relations
	 * (LAZY).
	 * 
	 * @param Collection {@link entities} with Objects of Model-Classes provided
	 *                   through the Config
	 * @param depth      requested load depth
	 */
	void resolveAllLazyLoaded(Collection<? extends Object> entities, Integer depth);

	/**
	 * Reloads the Values and Relations of {@link entity}.
	 * 
	 * @param entity entity Object of Model-Class provided through the Config
	 */
	void reload(Object entity);

	/**
	 * Reloads the Values and Relations of {@link entity}.
	 * <p>
	 * Depth defines how often the requested Objects Relations will be loaded
	 * recursively. <code>0</code> will only reload the Values and skip reloading
	 * any Relations.
	 * 
	 * @param entity entity Object of Model-Class provided through the Config
	 * @param depth  requested reload depth
	 */
	void reload(Object entity, Integer depth);

	/**
	 * Reloads the Values and Relations of Collection {@link entities}.
	 * 
	 * @param entities Collection of Model-Classes provided through the Config
	 */
	void reloadAll(Collection<? extends Object> entities);

	/**
	 * Reloads the Values and Relations of Collection {@link entities}.
	 * <p>
	 * Depth defines how often the requested Objects Relations will be loaded
	 * recursively. <code>0</code> will only reload the Values and skip reloading
	 * any Relations.
	 * 
	 * @param Collection {@link entities} with Collection of Model-Classes provided
	 *                   through the Config
	 * @param depth      requested reload depth
	 */
	void reloadAll(Collection<? extends Object> entities, Integer depth);

	/**
	 * Saves the provided Object.
	 * 
	 * @param entity Object of Model-Class provided through the Config
	 */
	void save(Object entity);

	/**
	 * Saves the provided Object.
	 * <p>
	 * Depth defines how many of the Object's Relations will be saved recursively.
	 * <code>0</code> will save the Object without saving any Relations.
	 * 
	 * @param entity Object of Model-Class provided through the Config
	 * @param depth  requested load depth
	 */
	void save(Object entity, Integer depth);

	/**
	 * Saves the provided Object without any Relations.
	 * 
	 * @see Session#save(Object, Integer)
	 * 
	 * @param entity Object of Model-Class provided through the Config
	 */
	void saveLazy(Object entity);

	/**
	 * Saves all provided values of Collection {@link entities}.
	 * 
	 * @param Collection entities of Objects of Model-Classes provided through the
	 *                   Config
	 */
	void saveAll(Collection<? extends Object> entities);

	/**
	 * Saves the provided values of Collection {@link entities}.
	 * <p>
	 * Depth defines how many of the Object's Relations will be saved recursively.
	 * <code>0</code> will save the Object without saving any Relations.
	 * 
	 * @param Collection entities of Objects of Model-Classes provided through the
	 *                   Config
	 * @param depth      requested load depth
	 */
	void saveAll(Collection<? extends Object> entities, Integer depth);

	/**
	 * Saves the provided values of Collection {@link entities} without saving any
	 * Relations.
	 * 
	 * @see Session#saveAll(Collection, Integer)
	 * 
	 * @param Collection entities of Objects of Model-Classes provided through the
	 *                   Config
	 */
	void saveAllLazy(Collection<? extends Object> entities);

	/**
	 * Deletes the provided Object.
	 * 
	 * @param entity Object of Model-Class provided through the Config
	 */
	void delete(Object entity);

	/**
	 * Deletes all provided values of Collection {@link entities}.
	 * 
	 * @param Collection entities of Objects of Model-Classes provided through the
	 *                   Config
	 */
	void deleteAll(Collection<? extends Object> entities);

	/**
	 * Unloades the provided Object.
	 * <p>
	 * (Removes the provided Object and all Relations from the Buffer)
	 * 
	 * @param entity Object of Model-Class provided through the Config
	 */
	void unload(Object entity);

	/**
	 * Unloades all provided values of Collection {@link entities}.
	 * <p>
	 * (Removes all provided values of Collection {@link entities} and all fo their
	 * Relations from the Buffer)
	 * 
	 * @param Collection entities of Objects of Model-Classes provided through the
	 *                   Config
	 */
	void unloadAll(Collection<? extends Object> entities);

	/**
	 * Provides the configured {@link QueryBuilder} for the use in custom queries
	 * 
	 * @return configured {@link QueryBuilder} instance
	 */
	QueryBuilder getQueryBuilder();

	/**
	 * Creates a simple {@link Session} for direct interaction with a database.
	 * <p>
	 * The {@link Configuration} {@link cnf} should be use the Configuration
	 * provided with the Database-Module you wish to use (usually prefixed with the
	 * Database-Name).
	 * 
	 * @param cnf {@link Configuration} of the database
	 * @return {@link Session} for direct interaction with a database
	 * @throws Exception re-throws any Exception that might accure during
	 *                   {@link Pipeline} creation.
	 */
	@SuppressWarnings("resource")
	public static Session create(Configuration cnf) throws Exception {
		return new Pipeline(new DatabasePipelineFactory(cnf)).buildSession();
	}
}
