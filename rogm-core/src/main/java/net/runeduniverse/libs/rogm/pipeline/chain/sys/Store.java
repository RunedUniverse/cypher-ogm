package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.util.HashMap;
import java.util.Map;

public final class Store {
	private final Map<Class<?>, Object> dataMap = new HashMap<>();
	private Object last = null;

	protected Store(Object[] args) {
		for (Object data : args)
			if (data != null) {
				if (data instanceof Store)
					this.dataMap.putAll(((Store) data).dataMap);
				else
					this.dataMap.put(data.getClass(), data);
			}
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