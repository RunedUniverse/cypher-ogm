package net.runeduniverse.libs.rogm.pipeline;

import java.util.Collection;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.Entry;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.LoadState;
import net.runeduniverse.libs.rogm.lang.DatabaseCleaner;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDeleteContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.Chains;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityCollectionContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.IdContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.SaveContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class DatabaseChainRouter extends AChainRouter {

	protected IBuffer buffer;
	protected DatabaseCleaner dbCleaner;
	protected Parser.Instance parserInstance;
	protected Language.Instance langInstance;
	protected Module.Instance<?> moduleInstance;

	public DatabaseChainRouter initialize(final IBuffer buffer, final DatabaseCleaner dbCleaner,
			final Parser.Instance parserInstance, final Language.Instance langInstance,
			final Module.Instance<?> moduleInstance) {
		this.buffer = buffer;
		this.dbCleaner = dbCleaner;
		this.parserInstance = parserInstance;
		this.langInstance = langInstance;
		this.moduleInstance = moduleInstance;
		this.baseChainParamPool.add(this.buffer);
		this.baseChainParamPool.add(this.parserInstance);
		this.baseChainParamPool.add(this.langInstance);
		this.baseChainParamPool.add(this.moduleInstance);
		return this;
	}

	@Override
	public <E> E load(Class<E> entityType, IFilter filter, IdContainer id, DepthContainer depth) throws Exception {
		return super.callChain(Chains.LOAD_CHAIN.ONE.LABEL, entityType, filter, id, depth, new LazyEntriesContainer());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Collection<E> loadAll(Class<E> entityType, IFilter filter, DepthContainer depth) throws Exception {
		return super.callChain(Chains.LOAD_CHAIN.ALL.LABEL, Collection.class, filter, depth,
				new LazyEntriesContainer());
	}

	@Override
	public void resolveAllLazyLoaded(Collection<? extends Object> entities, DepthContainer depth) throws Exception {
		LazyEntriesContainer lazyEntries = new LazyEntriesContainer();
		for (Object entity : entities) {
			Entry entry = this.buffer.getEntry(entity);
			if (entry == null || entry.getLoadState() == LoadState.COMPLETE)
				continue;
			lazyEntries.addEntry(entry);
		}

		super.callChain(Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, Collection.class, lazyEntries, depth);
	}

	@Override
	public void reloadAll(Collection<Object> entities, DepthContainer depth) throws Exception {
		super.callChain(Chains.RELOAD_CHAIN.ALL.LABEL, Void.class, new EntityCollectionContainer(entities), depth);
	}

	@Override
	public void save(EntityContainer entity, SaveContainer container, DepthContainer depth) throws Exception {
		super.callChain(Chains.SAVE_CHAIN.ONE.LABEL, Void.class, this.dbCleaner, entity, container, depth);
	}

	@Override
	public void delete(EntityContainer entity, /* IDeleteContainer container, */ DepthContainer depth)
			throws Exception {
		IBuffer.Entry entry = buffer.getEntry(entity.getEntity());
		if (entry == null)
			throw new Exception("Entity of type<" + entity.getType()
					.getName() + "> is not loaded!");

		IDeleteContainer container = this.archive.delete(entity.getType(), entry.getId(), entity.getEntity());
		Language.IDeleteMapper mapper = this.langInstance.delete(container.getDeleteFilter(),
				container.getEffectedFilter());
		mapper.updateBuffer(this.buffer, container.getDeletedId(), this.moduleInstance.query(mapper.effectedQry())
				.getRawData());
		this.moduleInstance.execute(mapper.qry());
	}

	@Override
	public void unload(Object entity) {
		this.buffer.removeEntry(entity);
	}

}
