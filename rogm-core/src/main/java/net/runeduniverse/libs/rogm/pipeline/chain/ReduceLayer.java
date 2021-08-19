package net.runeduniverse.libs.rogm.pipeline.chain;

import java.util.Collection;

public interface ReduceLayer {

	@Chain(label = Chain.LOAD_ONE_CHAIN, layers = { 450 }) // TODO FIX layers
	public static <T> T convertRecord(Collection<T> collection) {
		for (T t : collection)
			return t;
		return null;
	}
}
