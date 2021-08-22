package net.runeduniverse.libs.rogm.pipeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.Entry;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.LoadState;
import net.runeduniverse.libs.rogm.error.ExceptionSurpression;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDeleteContainer;
import net.runeduniverse.libs.rogm.pattern.IPattern.ISaveContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.AssemblyLayers;
import net.runeduniverse.libs.rogm.pipeline.chain.LookupLayers;
import net.runeduniverse.libs.rogm.pipeline.chain.ReduceLayer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.IdContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainManager;
import net.runeduniverse.libs.rogm.pattern.IQueryPattern;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;

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

	public abstract void resolveAllLazyLoaded(Collection<? extends Object> entities, Integer depth) throws Exception;

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

	// TODO FIX
	public void reloadAll(Collection<Object> entities, int depth) {
		// IBuffer.Entry entry = this.buffer.getEntry(entity);
		// return entry.getPattern().search(entry.getId(), depth == 0).getResult();
		Set<Entry> stage = new HashSet<>();

		for (Object entity : entities)
			if (entity != null)
				try {
					IBuffer buffer = null;
					Entry entry = buffer.getEntry(entity);
					IFilter filter = archive.search(entry.getType(), entry.getId(), depth == 0)
							.getResult();

					this._reloadObject(entity, filter, depth < 2 ? null : stage);
				} catch (Exception e) {
					this.logger.log(Level.WARNING, "Loading of Class<" + entity.getClass()
							.getCanonicalName() + "> Entity" + " (depth=" + depth + ") failed!", e);
				}

		for (int i = 0; i < depth - 1; i++) {
			if (stage.isEmpty())
				return;
			this._reloadRelatedObjects(stage);
		}
	}

	private void _reloadRelatedObjects(Set<Entry> stage) {
		Set<Entry> next = new HashSet<>();
		for (Entry entry : stage)
			if (entry != null)
				try {
					_reloadObject(entry.getEntity(), this.storage.search(entry.getType(), entry.getId(), false), next);
				} catch (Exception e) {
					this.logger.log(Level.WARNING, "Resolving of reloaded-related Buffer-Entry failed!", e);
				}
		stage.clear();
		stage.addAll(next);
	}

	private void _reloadObject(Object entity, IFilter filter, Set<Entry> relatedEntities) {
		try {
			Language.ILoadMapper m = lang.load(filter);
			IPattern.IDataRecord record = m.parseDataRecord(this.module.queryObject(m.qry()));

			this.storage.update(entity, record, relatedEntities);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Reloading of Class<" + entity.getClass()
					.getCanonicalName() + "> Entity failed!", e);
		}
	}

	// TODO FIX
	public void save(Object entity, int depth) throws Exception {
		if (entity == null)
			return;

		ISaveContainer container = this.getPattern(entity.getClass())
				.save(entity, depth);
		Language.ISaveMapper mapper = this.lang.save(container.getDataContainer(), container.getRelatedFilter());
		mapper.updateObjectIds(this.buffer, this.module.execute(mapper.qry()), LoadState.get(depth == 0));
		if (0 < depth) {
			Collection<String> ids = mapper.reduceIds(this.buffer, this.module);
			if (!ids.isEmpty())
				this.module.execute(this.lang.deleteRelations(ids));
		}
		container.postSave();

	}

	// TODO FIX
	public void delete(Object entity, int depth) throws Exception {

		IPattern.IDeleteContainer container = this.getPattern(entity.getClass())
				.delete(entity);
		Language.IDeleteMapper mapper = this.lang.delete(container.getDeleteFilter(), container.getEffectedFilter());
		mapper.updateBuffer(this.buffer, container.getDeletedId(), this.module.query(mapper.effectedQry()));
		this.module.execute(mapper.qry());
	}
}
