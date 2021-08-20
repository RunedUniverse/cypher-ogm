package net.runeduniverse.libs.rogm.pipeline.chain;

import java.util.Collection;

import net.runeduniverse.libs.rogm.pipeline.chain.data.Result;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chain;

public interface ReduceLayer {

	@Chain(label = Chain.LOAD_ONE_CHAIN, layers = { 450 }, ignoreCancelled = true) // TODO FIX layers
	public static <T> T convertRecord(Collection<T> collection, Result<T> result) {
		for (T t : collection) {
			result.setResult(t);
			return t;
		}
		return null;
	}
}
