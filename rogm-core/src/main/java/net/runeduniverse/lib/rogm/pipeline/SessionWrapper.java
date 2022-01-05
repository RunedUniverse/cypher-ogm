/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.rogm.pipeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import net.runeduniverse.lib.rogm.Session;
import net.runeduniverse.lib.rogm.info.SessionInfo;
import net.runeduniverse.lib.rogm.logging.PipelineLogger;
import net.runeduniverse.lib.rogm.logging.SessionLogger;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.rogm.querying.QueryBuilder;

public final class SessionWrapper implements Session {

	private final Pipeline pipeline;
	private final APipelineFactory<?> factory;
	private final AChainRouter router;
	private final SessionLogger logger;

	protected SessionWrapper(final Pipeline pipeline, final APipelineFactory<?> factory,
			final PipelineLogger pipelineLogger, final SessionInfo info) {
		this.pipeline = pipeline;
		this.factory = factory;
		this.router = this.factory.getRouter();
		this.logger = new SessionLogger(SessionWrapper.class, pipelineLogger).logSessionInfo(info);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	@Override
	public void close() throws Exception {
		this.pipeline.closeConnections(this);
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
	public <T> T load(IFilter filter) {
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
		try {
			return this.router.loadAll(filter);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Loading of Class Entities by custom Filter failed!", e);
		}
		return Collections.emptyList();
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
		this.resolveAllLazyLoaded(entities, 1);
	}

	@Override
	public void resolveAllLazyLoaded(Collection<? extends Object> entities, Integer depth) {
		try {
			this.router.resolveAllLazyLoaded(entities, depth);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Resolving of lazy loaded Buffer-Entries failed!", e);
		}
	}

	@Override
	public void reload(Object entity) {
		Set<Object> set = new HashSet<Object>();
		set.add(entity);
		try {
			this.router.reloadAll(set, 1);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Reloading of Class-Entity of Class<" + entity.getClass()
					.getCanonicalName() + "> failed!", e);
		}
	}

	@Override
	public void reload(Object entity, Integer depth) {
		Set<Object> set = new HashSet<Object>();
		set.add(entity);
		try {
			this.router.reloadAll(set, depth);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Reloading of Class-Entity of Class<" + entity.getClass()
					.getCanonicalName() + "> failed!", e);
		}
	}

	@Override
	public void reloadAll(Collection<? extends Object> entities) {
		try {
			this.router.reloadAll(new HashSet<Object>(entities), 1);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Reloading of Class-Entities failed!", e);
		}
	}

	@Override
	public void reloadAll(Collection<? extends Object> entities, Integer depth) {
		try {
			this.router.reloadAll(new HashSet<Object>(entities), depth);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Reloading of Class-Entities failed!", e);
		}
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
		try {
			this.router.delete(entity, 1);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Deletion of Class-Entity of Type<" + entity.getClass()
					.getCanonicalName() + "> failed!", e);
		}
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
		return this.factory.getQueryBuilder();
	}

}
