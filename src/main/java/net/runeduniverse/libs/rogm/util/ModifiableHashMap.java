package net.runeduniverse.libs.rogm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
	public void forEach(BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, M>> entry : this.map.entrySet()) {
			action.accept(entry.getKey(), entry.getValue().getValue());
		}
	}

	@Override
	public void forEach(M modifier, BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, M>> entry : this.map.entrySet())
			if (entry.getValue().getModifier() == modifier)
				action.accept(entry.getKey(), entry.getValue().getValue());
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
