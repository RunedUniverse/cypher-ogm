package net.runeduniverse.libs.rogm.pipeline.chains;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;

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

	public <R> R call(Class<R> resultType, Object[] args) throws Exception {
		Store store = new Store(args);
		for (ChainLayer layer : this.chain.values())
			layer.call(store);
		return store.getData(resultType);
	}

	public final class Store {
		protected final Map<Class<?>, Object> dataMap = new HashMap<>();

		protected Store(Object[] args) {
			for (Object data : args)
				if (data != null)
					this.dataMap.put(data.getClass(), data);
		}

		public void putData(Class<?> returnType, Object object) {
			if (object == null) {
				if (returnType == null)
					return;
				this.dataMap.put(returnType, object);
			} else
				this.dataMap.put(object.getClass(), object);
		}

		@SuppressWarnings("unchecked")
		public <T> T getData(Class<T> type) {
			if (type == null)
				return null;
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
	}
}
