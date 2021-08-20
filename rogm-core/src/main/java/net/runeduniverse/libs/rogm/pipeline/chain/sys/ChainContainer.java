package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import net.runeduniverse.libs.rogm.pipeline.chain.data.Result;

public final class ChainContainer {

	@Getter
	private final String label;
	private final Map<Integer, ILayer> chain = new TreeMap<>();

	protected ChainContainer(String label) {
		this.label = label;
	}

	protected void putAtLayers(final int[] ids, final ILayer layer) {
		for (int id : ids)
			this.chain.put(id, layer);
	}

	/***
	 * call(...) calls the chain and returns the Object defined by the resultType.
	 * In case no castable Entity for the resultType got returned or added to the
	 * Store, null is returned. In case resultType is null, the result of the last
	 * layer in the chain gets returned!
	 * 
	 * @param resultType
	 * @param args
	 * @return <R> R
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <R> R callChain(Class<R> resultType, Object[] args) throws Exception {
		// init
		Store store = new Store(args);
		Result<R> result = new Result<>(resultType);
		store.putData(Result.class, result);
		ChainRuntime runtime = new ChainRuntime();

		// runtime
		for (ILayer layer : this.chain.values()) {
			if (!result.hasResult() || ChainLayer.ignoreCancelled(layer))
				layer.call(store);
		}

		// return result
		if (resultType == null)
			return (R) store.getLastAdded();
		else if (result.hasResult())
			return result.getResult();
		return store.getData(resultType);
	}

		for (ILayer layer : this.chain.values()) {
			if (!result.hasResult() || ChainLayer.ignoreCancelled(layer))
				layer.call(store);
		}
		if (resultType == null)
			return (R) store.getLastAdded();
		else if (result.hasResult())
			return result.getResult();
		return store.getData(resultType);
	}
}
