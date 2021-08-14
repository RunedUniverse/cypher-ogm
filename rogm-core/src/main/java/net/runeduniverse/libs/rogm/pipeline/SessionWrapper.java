package net.runeduniverse.libs.rogm.pipeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import net.runeduniverse.libs.rogm.Session;
import net.runeduniverse.libs.rogm.info.SessionInfo;
import net.runeduniverse.libs.rogm.logging.PipelineLogger;
import net.runeduniverse.libs.rogm.logging.SessionLogger;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;

public final class SessionWrapper implements Session {

	private final ATransactionFactory factory;
	private final ATransactionRouter router;
	private final SessionLogger logger;

	protected SessionWrapper(final ATransactionFactory factory, final ATransactionRouter router,
			final PipelineLogger pipelineLogger, final SessionInfo info) {
		this.factory = factory;
		this.router = router;
		this.logger = new SessionLogger(SessionWrapper.class, pipelineLogger, info).logConfig();
	}

	@Override
	public void close() throws Exception {
		this.factory.closeConnections();
	}

	@Override
	public boolean isConnected() {
		return this.factory.isConnected();
	}

	private <T, ID extends Serializable> T _load(Class<T> type, ID id, Integer depth) {

		try {
			return (T) this.router.load(type, id, depth);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Loading of Class<" + type.getCanonicalName() + "> Entity with id=" + id
					+ " (depth=" + depth + ") failed!", e);
		}
		return null;
	}

	private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, ID id, Integer depth) {
		try {
			return this.router.loadAll(type, id, depth);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Loading of Class<" + type.getCanonicalName() + "> Entities with id=" + id
					+ " (depth=" + depth + ") failed!", e);
		}
		return new ArrayList<T>();
	}

	private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, Integer depth) {
		try {
			return this.router.loadAll(type, depth);
		} catch (Exception e) {
			this.logger.log(Level.WARNING,
					"Loading of Class<" + type.getCanonicalName() + "> Entities" + " (depth=" + depth + ") failed!", e);
		}
		return new ArrayList<T>();
	}

	private void _save(Object entity, Integer depth) {
		try {
			this.router.save(entity, depth);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Saving of Class<" + entity.getClass()
					.getCanonicalName() + "> Entity failed! (depth=" + depth + ')', e);
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public <T, ID extends Serializable> T load(Class<T> type, ID id) {
		return this._load(type, id, 1);
	}

	@Override
	public <T, ID extends Serializable> T load(Class<T> type, ID id, Integer depth) {
		if (depth < 0)
			depth = 0;
		return this._load(type, id, depth);
	}

	@Override
	public <T, ID extends Serializable> T loadLazy(Class<T> type, ID id) {
		return this._load(type, id, 0);
	}

	@Override
	public <T, ID extends Serializable> T load(IFilter filter) {
		try {
			return this.router.load(filter);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Loading of Class Entity by custom Filter failed!", e);
		}
		return null;
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id) {
		return this._loadAll(type, id, 1);
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id, Integer depth) {
		if (depth < 0)
			depth = 0;
		return this._loadAll(type, id, depth);
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAllLazy(Class<T> type, ID id) {
		return this._loadAll(type, id, 0);
	}

	@Override
	public <T> Collection<T> loadAll(Class<T> type) {
		return this._loadAll(type, 1);
	}

	@Override
	public <T> Collection<T> loadAll(Class<T> type, Integer depth) {
		if (depth < 0)
			depth = 0;
		return this._loadAll(type, depth);
	}

	@Override
	public <T> Collection<T> loadAllLazy(Class<T> type) {
		return this._loadAll(type, 0);
	}

	@Override
	public <T> Collection<T> loadAll(IFilter filter) {
		return this.router.loadAll(filter);
	}

	@Override
	public void resolveLazyLoaded(Object entity) {
		this.resolveAllLazyLoaded(Arrays.asList(entity), 1);
	}

	@Override
	public void resolveLazyLoaded(Object entity, Integer depth) {
		this.resolveAllLazyLoaded(Arrays.asList(entity), depth);
	}

	@Override
	public void resolveAllLazyLoaded(Collection<? extends Object> entities) {
		this.router.resolveAllLazyLoaded(entities, 1);
	}

	@Override
	public void resolveAllLazyLoaded(Collection<? extends Object> entities, Integer depth) {
		this.router.resolveAllLazyLoaded(entities, depth);
	}

	@Override
	public void reload(Object entity) {
		Set<Object> set = new HashSet<Object>();
		set.add(entity);
		this.router.reloadAll(set, 1);
	}

	@Override
	public void reload(Object entity, Integer depth) {
		Set<Object> set = new HashSet<Object>();
		set.add(entity);
		this.router.reloadAll(set, depth);
	}

	@Override
	public void reloadAll(Collection<? extends Object> entities) {
		this.router.reloadAll(new HashSet<Object>(entities), 1);
	}

	@Override
	public void reloadAll(Collection<? extends Object> entities, Integer depth) {
		this.router.reloadAll(new HashSet<Object>(entities), depth);
	}

	@Override
	public void save(Object entity) {
		this._save(entity, 1);
	}

	@Override
	public void save(Object entity, Integer depth) {
		if (depth < 0)
			depth = 0;
		this._save(entity, depth);
	}

	@Override
	public void saveLazy(Object entity) {
		this._save(entity, 0);
	}

	@Override
	public void saveAll(Collection<? extends Object> entities) {
		for (Object e : entities)
			this._save(e, 1);
	}

	@Override
	public void saveAll(Collection<? extends Object> entities, Integer depth) {
		if (depth < 0)
			depth = 0;
		for (Object e : entities)
			this._save(e, depth);
	}

	@Override
	public void saveAllLazy(Collection<? extends Object> entities) {
		for (Object e : entities)
			this._save(e, 0);
	}

	@Override
	public void delete(Object entity) {

	}

	@Override
	public void deleteAll(Collection<? extends Object> entities) {
		for (Object e : entities)
			this.delete(e);
	}

	@Override
	public void unload(Object entity) {
		this.router.unload(entity);
	}

	@Override
	public void unloadAll(Collection<? extends Object> entities) {
		for (Object object : entities)
			this.unload(object);
	}

	@Override
	public QueryBuilder getQueryBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

}