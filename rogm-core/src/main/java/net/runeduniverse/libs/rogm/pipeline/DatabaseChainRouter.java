package net.runeduniverse.libs.rogm.pipeline;

import java.util.Collection;
import java.util.logging.Logger;

import net.runeduniverse.libs.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.BufferTypes.IEntry;
import net.runeduniverse.libs.rogm.buffer.BufferTypes.LoadState;
import net.runeduniverse.libs.rogm.lang.DatabaseCleaner;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
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

	public DatabaseChainRouter() {
		this(null);
	}

	public DatabaseChainRouter(Logger parent) {
		super(new UniversalLogger(DatabaseChainRouter.class, null));
	}

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
			IEntry entry = this.buffer.getEntry(entity);
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
	public void delete(EntityContainer entity, DepthContainer depth) throws Exception {
		super.callChain(Chains.DELETE_CHAIN.ONE.LABEL, Void.class, entity, depth);
	}

	@Override
	public void unload(Object entity) {
		this.buffer.removeEntry(entity);
	}

}
