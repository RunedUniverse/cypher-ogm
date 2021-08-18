package net.runeduniverse.libs.rogm.pipeline.chains;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public final class ChainContainer {

	@Getter
	private final String label;
	private final Map<Integer, ChainLayer> chain = new TreeMap<>();

	protected ChainContainer(String label) {
		this.label = label;
	}

	public void putAtLayers(final int[] ids, final ChainLayer layer) {
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
	public <R> R call(Class<R> resultType, Object[] args) throws Exception {
		Store store = new Store(args);
		store.putData(ResultType.class, new ResultType<>(resultType));
		for (ChainLayer layer : this.chain.values())
			layer.call(store);
		if (resultType == null)
			return (R) store.getLastAdded();
		return store.getData(resultType);
	}

	@RequiredArgsConstructor
	public final class ResultType<R> {
		private final Class<R> type;

		public Class<R> getType() {
			return this.type;
		}
	}

	public final class Store {
		private final Map<Class<?>, Object> dataMap = new HashMap<>();
		private Object last = null;

		protected Store(Object[] args) {
			for (Object data : args)
				if (data != null)
					this.dataMap.put(data.getClass(), data);
		}

		public void putData(Class<?> dataType, Object data) {
			if (data == null) {
				if (dataType == null)
					return;
				this.dataMap.put(dataType, data);
			} else
				this.dataMap.put(data.getClass(), data);
			this.last = data;
		}

		@SuppressWarnings("unchecked")
		public <T> T getData(Class<T> type) {
			if (type == null)
				return null;
			if (type.equals(Store.class))
				return (T) this;
			Object obj = this.dataMap.get(type);
			if (obj != null)
				return (T) obj;

			for (Class<?> clazz : this.dataMap.keySet())
				if (type.isAssignableFrom(clazz))
					return (T) this.dataMap.get(clazz);
			return null;
		}

		public Object[] getData(Class<?>[] paramTypes) {
			Object[] arr = new Object[paramTypes.length];
			for (int i = 0; i < paramTypes.length; i++)
				arr[0] = this.getData(paramTypes[i]);
			return arr;
		}

		public Object getLastAdded() {
			return this.last;
		}
	}
}
