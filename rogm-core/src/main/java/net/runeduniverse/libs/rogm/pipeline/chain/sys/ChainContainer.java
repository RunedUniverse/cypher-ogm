package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import lombok.Getter;
import net.runeduniverse.libs.rogm.error.ExceptionSurpression;

public final class ChainContainer {

	@Getter
	private final ChainManager manager;
	@Getter
	private final String label;
	private final Map<Integer, ILayer> chain = new TreeMap<>();

	protected ChainContainer(ChainManager manager, String label) {
		this.manager = manager;
		this.label = label;
	}

	protected void putAtLayers(final int[] ids, final ILayer layer) {
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
		Set<Exception> errors = new HashSet<>();
		boolean noErrors = true;
		for (ILayer layer : this.chain.values()) {
			try {
				if ((noErrors || ChainLayer.ignoreErrors(layer))
						&& (runtime.active() || ChainLayer.ignoreInActive(layer)))
					layer.call(runtime);
			} catch (Exception e) {
				errors.add(e);
				noErrors = false;
			}
			if (!noErrors)
				throw new ExceptionSurpression("Chain<" + this.label + "> errored out!").addSuppressed(errors);
		}
		return runtime.getFinalResult();
	}

}
