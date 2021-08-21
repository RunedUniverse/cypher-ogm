package net.runeduniverse.libs.rogm.pipeline;

import java.util.Collection;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pipeline.chain.Chains;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.IdContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class DatabaseChainRouter extends AChainRouter {

	protected Parser.Instance parserInstance;
	protected Language.Instance langInstance;
	protected Module.Instance<?> moduleInstance;

	public DatabaseChainRouter initialize(final Parser.Instance parserInstance, final Language.Instance langInstance,
			final Module.Instance<?> moduleInstance) {
		this.parserInstance = parserInstance;
		this.langInstance = langInstance;
		this.moduleInstance = moduleInstance;
		this.baseChainParamPool.add(this.parserInstance);
		this.baseChainParamPool.add(this.langInstance);
		this.baseChainParamPool.add(this.moduleInstance);
		return this;
	}

	@Override
	public <E> E load(Class<E> entityType, IFilter filter, IdContainer id, DepthContainer depth) throws Exception {
		return super.callChain(Chains.LOAD_CHAIN.ONE.LABEL, entityType, filter, id, depth);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Collection<E> loadAll(Class<E> entityType, IFilter filter, DepthContainer depth) throws Exception {
		return super.callChain(Chains.LOAD_CHAIN.ALL.LABEL, Collection.class, filter, depth);
	}

}
