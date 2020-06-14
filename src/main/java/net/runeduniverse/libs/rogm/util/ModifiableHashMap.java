package net.runeduniverse.libs.rogm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import lombok.Data;

public class ModifiableHashMap<K, V, M> implements ModifiableMap<K, V, M> {

	private Map<K, MEntry<V, M>> map = new HashMap<>();

	@Override
	public V put(K key, V value) {
		return this.map.put(key, new MEntry<>(value)).getValue();
	}

	@Override
	public V put(K key, V value, M modifier) {
		return this.map.put(key, new MEntry<>(value, modifier)).getValue();
	}

	@Override
	public V get(K key) {
		return this.map.get(key).getValue();
	}

	@Override
	public void setModifier(K key, M modifier) {
		this.map.get(key).setModifier(modifier);
	}

	@Override
	public M getModifier(K key) {
		MEntry<V, M> entry = this.map.get(key);
		if (entry == null)
			return null;
		return entry.getModifier();
	}

	@Override
	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}

	@Override
	public boolean containsKey(K key, M modifier) {
		MEntry<V, M> entry = this.map.get(key);
		if (entry == null)
			return false;
		return entry.getModifier().equals(modifier);
	}

	@Override
	public boolean containsValue(V value) {
		for (MEntry<V, M> me : this.map.values())
			if (me.getValue().equals(value))
				return true;
		return false;
	}

	@Override
	public boolean containsValue(V value, M modifier) {
		for (MEntry<V, M> me : this.map.values())
			if (me.getModifier().equals(modifier) && me.getValue().equals(value))
				return true;
		return false;
	}

	@Override
	public void forEach(BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, M>> entry : this.map.entrySet()) {
			action.accept(entry.getKey(), entry.getValue().getValue());
		}
	}

	@Override
	public void forEach(M modifier, BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, M>> entry : this.map.entrySet())
			if (entry.getValue().getModifier().equals(modifier))
				action.accept(entry.getKey(), entry.getValue().getValue());
	}

	@Override
	public Set<K> keySet() {
		return this.map.keySet();
	}

	@SuppressWarnings("hiding")
	@Data
	protected class MEntry<V, M> {
		private V value;
		private M modifier;

		protected MEntry(V value) {
			this.value = value;
			this.modifier = null;
		}

		protected MEntry(V value, M modifier) {
			this.value = value;
			this.modifier = modifier;
		}
	}

}
