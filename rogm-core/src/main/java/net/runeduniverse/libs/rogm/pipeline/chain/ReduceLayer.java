package net.runeduniverse.libs.rogm.pipeline.chain;

import java.util.Collection;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.Entry;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chain;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainRuntime;

public interface ReduceLayer {

	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = {
			Chains.LOAD_CHAIN.ONE.REDUCE_COLLECTION }, ignoreResult = true)
	public static <T> T reduceCollection(final ChainRuntime<T> runtime, Collection<T> collection) {
		for (T t : collection) {
			runtime.setResult(t);
			return t;
		}
		return null;
	}

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = { Chains.LOAD_CHAIN.ALL.RESOLVE_DEPTH }, ignoreResult = true)
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.RESOLVE_DEPTH }, ignoreResult = true)
	public static void resolveDepth(final ChainRuntime<?> runtime, DepthContainer depth) throws Exception {
		if (depth.getDepth() < 2)
			return;
		depth.subtractOne();
		runtime.callSubChainWithSourceData(Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, null);
	}

	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.VALIDATE_LAZY_ENTRIES })
	public static void validateLazyEntries(final ChainRuntime<?> runtime, final LazyEntriesContainer lazyEntries,
			final DepthContainer depth) {
		if (lazyEntries.isEmpty() || depth == null || depth.getDepth() == 0)
			runtime.setCanceled(true);
	}

	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.CALL_RESOLVE_SELECTED })
	public static void resolveLazyEntries(final ChainRuntime<?> runtime, final Archive archive,
			final LazyEntriesContainer lazyEntries) throws Exception {
		LazyEntriesContainer nextLazyEntries = new LazyEntriesContainer();
		for (Entry entry : lazyEntries.getLazyEntries()) {
			runtime.callSubChainWithSourceData(Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL, Collection.class,
					archive.search(entry.getType(), entry.getId(), false)
							.getResult(),
					nextLazyEntries);
		}
		lazyEntries.clear();
		lazyEntries.addEntries(nextLazyEntries);
	}

	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LOOP_LAZY_ENTRIES })
	public static void loopLazyEntries(final ChainRuntime<?> runtime, final DepthContainer depth) {
		depth.subtractOne();
		if (0 < depth.getDepth())
			runtime.jumpToLayer(Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.VALIDATE_LAZY_ENTRIES);
	}
}
