/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.runeduniverse.lib.rogm.pattern.Archive;
import net.runeduniverse.lib.rogm.pattern.IPattern.IPatternContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.IdContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.SaveContainer;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.utils.chain.ChainManager;
import net.runeduniverse.lib.utils.logging.UniversalLogger;

public abstract class AChainRouter {

	protected final Set<Object> baseChainParamPool = new HashSet<>();
	protected final UniversalLogger logger;
	protected Archive archive;
	protected ChainManager manager;

	protected AChainRouter() {
		this(new UniversalLogger(AChainRouter.class, null));
	}

	protected AChainRouter(UniversalLogger logger) {
		this.logger = logger;
		this.baseChainParamPool.add(logger);
	}

	public AChainRouter initialize(final Archive archive) {
		this.archive = archive;
		this.baseChainParamPool.add(this.archive);
		return this;
	}

	public void setChainManager(final ChainManager manager) {
		this.manager = manager;
	}

	public abstract <E> E load(Class<E> entityType, IFilter filter, IdContainer id, DepthContainer depth)
			throws Exception;

	public abstract <E> Collection<E> loadAll(Class<E> entityType, IFilter filter, DepthContainer depth)
			throws Exception;

	public abstract void resolveAllLazyLoaded(Collection<? extends Object> entities, DepthContainer depth)
			throws Exception;

	public abstract void reloadAll(Collection<Object> entities, DepthContainer depth) throws Exception;

	public abstract void save(EntityContainer entity, SaveContainer container, DepthContainer depth) throws Exception;

	public abstract void delete(EntityContainer entity, DepthContainer depth) throws Exception;

	public abstract void unload(Object entity);

	// internal Helper
	protected <R> R callChain(String label, Class<R> resultType, Object... args) throws Exception {
		int size = this.baseChainParamPool.size();
		Object[] arr = new Object[size + args.length];
		this.baseChainParamPool.toArray(arr);
		for (int i = 0; i < args.length; i++)
			arr[size + i] = args[i];
		return this.manager.callChain(label, resultType, arr);
	}

	// Route Invocation from Session Wrapper
	@SuppressWarnings("unchecked")
	public <E> E load(IFilter filter) throws Exception {
		return (E) this.load(getEntityOrObjectType(filter), filter, null, new DepthContainer(1));
	}

	public <E> Collection<E> loadAll(IFilter filter) throws Exception {
		return this.loadAll(null, filter, new DepthContainer(1));
	}

	public <E> E load(Class<E> entityType, int depth) throws Exception {
		return load(entityType, this.archive.search(entityType, depth == 0)
				.getResult(), null, new DepthContainer(depth));
	}

	public <E> E load(Class<E> entityType, Serializable id, int depth) throws Exception {
		return load(entityType, this.archive.search(entityType, id, depth == 0)
				.getResult(), new IdContainer(id), new DepthContainer(depth));
	}

	public <E> Collection<E> loadAll(Class<E> entityType, int depth) throws Exception {
		return loadAll(entityType, this.archive.search(entityType, depth == 0)
				.getResult(), new DepthContainer(depth));
	}

	public <E> Collection<E> loadAll(Class<E> entityType, Serializable id, int depth) throws Exception {
		return loadAll(entityType, this.archive.search(entityType, id, depth == 0)
				.getResult(), new DepthContainer(depth));
	}

	public void resolveAllLazyLoaded(Collection<? extends Object> entities, int depth) throws Exception {
		resolveAllLazyLoaded(entities, new DepthContainer(depth));
	}

	public void reloadAll(Collection<Object> entities, int depth) throws Exception {
		reloadAll(entities, new DepthContainer(depth));
	}

	public void save(Object entity, int depth) throws Exception {
		if (entity == null)
			return;
		save(new EntityContainer(entity), this.archive.save(entity.getClass(), entity, depth),
				new DepthContainer(depth));
	}

	public void delete(Object entity, int depth) throws Exception {
		if (entity == null)
			return;
		delete(new EntityContainer(entity), new DepthContainer(depth));
	}

	protected static Class<?> getEntityOrObjectType(IFilter filter) {
		if (filter instanceof IPatternContainer)
			return ((IPatternContainer) filter).getPattern()
					.getType();
		return Object.class;
	}
}
