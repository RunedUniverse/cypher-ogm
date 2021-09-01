package net.runeduniverse.libs.rogm.pipeline;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.IdContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.SaveContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainManager;
import net.runeduniverse.libs.rogm.querying.IFilter;

public abstract class AChainRouter {

	protected Set<Object> baseChainParamPool = new HashSet<>();
	protected Archive archive;
	protected ChainManager manager;

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

	public abstract void delete(EntityContainer entity, /* IDeleteContainer container, */ DepthContainer depth)
			throws Exception;

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
	public <E> E load(IFilter filter) throws Exception {
		return this.load(null, filter, null, new DepthContainer(1));
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
}