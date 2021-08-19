package net.runeduniverse.libs.rogm.pipeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.IBuffer.Entry;
import net.runeduniverse.libs.rogm.buffer.IBuffer.LoadState;
import net.runeduniverse.libs.rogm.error.ExceptionSurpression;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDeleteContainer;
import net.runeduniverse.libs.rogm.pattern.IPattern.ISaveContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.Assembler;
import net.runeduniverse.libs.rogm.pipeline.chain.ChainManager;
import net.runeduniverse.libs.rogm.pipeline.chain.LookupLayers;
import net.runeduniverse.libs.rogm.pipeline.chain.ReduceLayer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pattern.IQueryPattern;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;

public abstract class AChainRouter {
	static {
		ChainManager.addChainLayers(LookupLayers.class);
		ChainManager.addChainLayers(Assembler.class);
		ChainManager.addChainLayers(ReduceLayer.class);
	}

	protected Archive archive;
	protected Set<Object> baseChainParamPool = new HashSet<>();

	public AChainRouter initialize(Archive archive) {
		this.archive = archive;
		this.baseChainParamPool.add(this.archive);
		return this;
	}

	public abstract <E> E load(Class<E> entityType, IFilter filter, DepthContainer depth) throws Exception;

	public abstract <E> Collection<E> loadAll(Class<E> entityType, IFilter filter, DepthContainer depth)
			throws Exception;

	// internal helper
	protected IBaseQueryPattern _getBaseQueryPattern(final Class<?> entityType) {
		return this.archive.getPattern(entityType, IBaseQueryPattern.class);
	}

	protected <E> IQueryBuilder<?, ? extends IFilter> _loadFilterQueryPatter(final Class<E> entityType,
			final IQueryBuilder<?, ? extends IFilter> builder) throws Exception {
		for (IQueryPattern pattern : this.archive.getPatterns(entityType, IQueryPattern.class))
			pattern.search(builder);
		return builder;
	}

	protected <R> R callChain(String label, Class<R> resultType, Object... args) throws Exception {
		int size = this.baseChainParamPool.size();
		Object[] arr = new Object[size + args.length];
		this.baseChainParamPool.toArray(arr);
		for (int i = 0; i < args.length; i++)
			arr[size + i] = args[i];
		return ChainManager.callChain(label, resultType, arr);
	}

	// Route Invocation from Session Wrapper
	public <E> E load(IFilter filter) throws Exception {
		return this.load(null, filter, new DepthContainer(1));
	}
	public <E> Collection<E> loadAll(IFilter filter) throws Exception {
		return this.load(null, filter, new DepthContainer(1));
	}

	public <E> E load(Class<E> entityType, int depth) throws Exception {
		return load(entityType,
				_loadFilterQueryPatter(entityType, _getBaseQueryPattern(entityType).search(depth == 0)).getResult(),
				new DepthContainer(depth));
	}

	public <E> E load(Class<E> entityType, Serializable id, int depth) throws Exception {
		return load(entityType,
				_loadFilterQueryPatter(entityType, _getBaseQueryPattern(entityType).search(id, depth == 0)).getResult(),
				new DepthContainer(depth));
	}

	public <E> Collection<E> loadAll(Class<E> entityType, int depth) throws Exception {
		return loadAll(entityType,
				_loadFilterQueryPatter(entityType, _getBaseQueryPattern(entityType).search(depth == 0)).getResult(),
				new DepthContainer(depth));
	}

	public <E> Collection<E> loadAll(Class<E> entityType, Serializable id, int depth) throws Exception {
		return loadAll(entityType,
				_loadFilterQueryPatter(entityType, _getBaseQueryPattern(entityType).search(id, depth == 0)).getResult(),
				new DepthContainer(depth));
	}

	private <T, ID extends Serializable> T _load(Class<T> type, ID id, Integer depth) {
		T o;
		if (depth == 0)
			o = this.buffer.getByEntityId(id, type);
		else
			o = this.buffer.getCompleteByEntityId(id, type);
		if (o != null)
			return o;

		try {
			Collection<T> all = this._loadAll(type, id, depth);
			if (all.isEmpty())
				return null;
			else
				for (T t : all)
					return t;

		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Loading of Class<" + type.getCanonicalName() + "> Entity with id=" + id
					+ " (depth=" + depth + ") failed!", e);
		}
		return null;
	}

	private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, ID id, Integer depth) {
		try {
			return this._loadAll(type, this.storage.search(type, id, depth == 0), depth);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Loading of Class<" + type.getCanonicalName() + "> Entities with id=" + id
					+ " (depth=" + depth + ") failed!", e);
		}
		return new ArrayList<T>();
	}

	private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, Integer depth) {
		try {
			return this._loadAll(type, this.storage.search(type, depth == 0), depth);
		} catch (Exception e) {
			this.logger.log(Level.WARNING,
					"Loading of Class<" + type.getCanonicalName() + "> Entities" + " (depth=" + depth + ") failed!", e);
		}
		return new ArrayList<T>();
	}

	private <T> Collection<T> _loadAll(Class<T> type, IFilter filter, Integer depth) {
		if (depth < 2)
			return _loadAllObjects(type, filter, null);

		Set<Entry> stage = new HashSet<>();
		Collection<T> coll = _loadAllObjects(type, filter, stage);

		this._resolveAllLazyLoaded(stage, depth - 1);
		return coll;
	}

	public void resolveAllLazyLoaded(Collection<? extends Object> entities, Integer depth) {
		Set<Entry> stage = new HashSet<>();
		for (Object entity : entities) {
			Entry entry = this.buffer.getEntry(entity);
			if (entry == null || entry.getLoadState() == LoadState.COMPLETE)
				continue;
			stage.add(entry);
		}

		this._resolveAllLazyLoaded(stage, depth);
	}

	private void _resolveAllLazyLoaded(Set<Entry> stage, Integer depth) {
		for (int i = 0; i < depth; i++) {
			if (stage.isEmpty())
				return;
			this._resolveAllLazyLoaded(stage);
		}
	}

	private void _resolveAllLazyLoaded(Set<Entry> stage) {
		Set<Entry> next = new HashSet<>();
		for (Entry entry : stage)
			try {
				_loadAllObjects(entry.getType(), this.storage.search(entry.getType(), entry.getId(), false), next);
			} catch (Exception e) {
				this.logger.log(Level.WARNING, "Resolving of lazy loaded Buffer-Entry failed!", e);
			}
		stage.clear();
		stage.addAll(next);
	}

	private <T> Collection<T> _loadAllObjects(Class<T> type, IFilter filter, Set<Entry> lazyEntities) {
		try {
			Language.ILoadMapper m = lang.load(filter);
			IPattern.IDataRecord record = m.parseDataRecord(this.module.queryObject(m.qry()));

			return this.storage.parse(type, record, lazyEntities);
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Loading of Class<" + type.getCanonicalName() + "> Entities failed!", e);
			return new ArrayList<T>();
		}
	}

	// TODO FIX
	public void reloadAll(Collection<Object> entities, int depth) {
		// IBuffer.Entry entry = this.buffer.getEntry(entity);
		// return entry.getPattern().search(entry.getId(), depth == 0).getResult();
		Set<Entry> stage = new HashSet<>();

		for (Object entity : entities)
			if (entity != null)
				try {
					this._reloadObject(entity, this.storage.search(entity, depth == 0), depth < 2 ? null : stage);
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
	public ISaveContainer save(Object entity, int depth) throws Exception {
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
	public IDeleteContainer delete(Object entity, int depth) throws Exception {
		try {
			IPattern.IDeleteContainer container = this.getPattern(entity.getClass())
					.delete(entity);
			Language.IDeleteMapper mapper = this.lang.delete(container.getDeleteFilter(),
					container.getEffectedFilter());
			mapper.updateBuffer(this.buffer, container.getDeletedId(), this.module.query(mapper.effectedQry()));
			this.module.execute(mapper.qry());
		} catch (Exception e) {
			this.logger.log(Level.WARNING, "Deletion of Class<" + entity.getClass()
					.getCanonicalName() + "> Entity failed!", e);
		}
	}

	public void unload(Object entity) {
		this.buffer.removeEntry(entity);
	}

}
