package net.runeduniverse.libs.rogm.pipeline.chain;

import java.util.Collection;

import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chain;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainRuntime;

public interface ReduceLayer {

	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = {
			Chains.LOAD_CHAIN.ONE.REDUCE_COLLECTION }, ignoreResult = true) // TODO FIX layers
	public static <T> T reduceCollection(final ChainRuntime<T> runtime, Collection<T> collection) {
		for (T t : collection) {
			runtime.setResult(t);
			return t;
		}
		return null;
	}
}
