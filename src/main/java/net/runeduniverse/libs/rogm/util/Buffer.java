package net.runeduniverse.libs.rogm.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Buffer {

	private Map<? super Serializable, Object> buffer = new HashMap<>();

	public <T, ID extends Serializable> T load(ID id, Class<T> type) {
		Object result = this.buffer.get(id);

		if (result == null || !type.isAssignableFrom(result.getClass()))
			return null;

		return type.cast(result);
	}

	public <T, ID extends Serializable> void save(ID id, T object) {
		this.buffer.put(id, object);
	}

	public Object acquire(Serializable serializable, Class<?> type, Object object) {
		Object o = this.load(serializable, type);
		if (o != null)
			return o;
		this.save(serializable, object);
		return object;
	}

	public Collection<Object> getAll() {
		return this.buffer.values();
	}
}
