package net.runeduniverse.libs.rogm.pipeline;

import java.util.Collection;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.Entry;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.LoadState;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pipeline.chain.Chains;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityCollectionContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.IdContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class DatabaseChainRouter extends AChainRouter {

	protected IBuffer buffer;
	protected Parser.Instance parserInstance;
	protected Language.Instance langInstance;
	protected Module.Instance<?> moduleInstance;

	public DatabaseChainRouter initialize(final IBuffer buffer, final Parser.Instance parserInstance,
			final Language.Instance langInstance, final Module.Instance<?> moduleInstance) {
		this.buffer = buffer;
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
	public void resolveAllLazyLoaded(Collection<? extends Object> entities, Integer depth) throws Exception {
		LazyEntriesContainer lazyEntries = new LazyEntriesContainer();
		for (Object entity : entities) {
			Entry entry = this.buffer.getEntry(entity);
			if (entry == null || entry.getLoadState() == LoadState.COMPLETE)
				continue;
			lazyEntries.addEntry(entry);
		}

		super.callChain(Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, Collection.class, lazyEntries,
				new DepthContainer(depth));
	}

	@Override
	public void reloadAll(Collection<Object> entities, int depth) throws Exception {
		super.callChain(Chains.RELOAD_CHAIN.ALL.LABEL, Void.class, new EntityCollectionContainer(entities),
				new DepthContainer(depth));
	}

	@Override
	public void unload(Object entity) {
		this.buffer.removeEntry(entity);
	}

}
