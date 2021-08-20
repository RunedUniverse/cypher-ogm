package net.runeduniverse.libs.rogm.pipeline.chain;

import java.util.Collection;

import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chain;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Result;

public interface ReduceLayer {

	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = {
			Chains.LOAD_CHAIN.ONE.REDUCE_COLLECTION }, ignoreResult = true) // TODO FIX layers
	public static <T> T reduceCollection(Collection<T> collection, Result<T> result) {
		for (T t : collection) {
			result.setResult(t);
			return t;
		}
		return null;
	}
}
