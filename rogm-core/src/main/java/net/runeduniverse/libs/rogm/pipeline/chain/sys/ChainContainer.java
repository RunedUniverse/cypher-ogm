package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import net.runeduniverse.libs.rogm.error.ExceptionSurpression;

public final class ChainContainer {

	@Getter
	private final ChainManager manager;
	@Getter
	private final String label;
	private final Map<Integer, ILayer> chain = new HashMap<>();

	private int lowestId = Integer.MAX_VALUE;
	private int highestId = Integer.MIN_VALUE;
	private boolean dirty = false;

	protected ChainContainer(ChainManager manager, String label) {
		this.manager = manager;
		this.label = label;
	}

	protected void putAtLayers(final int[] ids, final ILayer layer) {
		this.dirty = true;
		for (int id : ids)
			this.chain.put(id, layer);
	}

	public <R> R callChain(Class<R> resultType, Object[] args) throws ExceptionSurpression {
		return this._callChain(new ChainRuntime<>(this, resultType, args));
	}

	public <R> R callChain(Class<R> resultType, ChainRuntime<?> rootRuntime, Map<Class<?>, Object> sourceDataMap,
			Object[] args) throws ExceptionSurpression {
		return this._callChain(new ChainRuntime<>(rootRuntime, this, resultType, sourceDataMap, args));
	}

	/***
	 * call(...) calls the chain and returns the Object defined by the resultType.
	 * 
	 * @param ChainRuntime<R> runtime
	 * @return returns the <code>Object</code> defined by the resultType or in case
	 *         the resultType is null the last returned (!null) value in the chain;
	 *         returns <code>null</code> if the chain got canceled or no castable
	 *         Entity for the resultType got returned/stored
	 * @throws <code>ExceptionSurpression</code>
	 */
	private <R> R _callChain(ChainRuntime<R> runtime) throws ExceptionSurpression {
		if (this.dirty)
			this.purify();
		runtime.executeOnChain(this.chain, this.lowestId, this.highestId);
		return runtime.getFinalResult();
	}

	private void purify() {
		this.lowestId = Integer.MAX_VALUE;
		this.highestId = Integer.MIN_VALUE;
		for (int id : this.chain.keySet()) {
			if (id < this.lowestId)
				this.lowestId = id;
			if (this.highestId < id)
				this.highestId = id;
		}
	}

}