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
		return MEntry.getValue(this.map.put(key, new MEntry<>(value)));
	}

	@Override
	public V put(K key, V value, M modifier) {
		return MEntry.getValue(this.map.put(key, new MEntry<>(value, modifier)));
	}

	@Override
	public V get(K key) {
		return MEntry.getValue(this.map.get(key));
	}

	@Override
	public void setModifier(K key, M modifier) {
		this.map.get(key).setModifier(modifier);
	}

	@Override
	public M getModifier(K key) {
		return MEntry.getModifier(this.map.get(key));
	}

	@Override
	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}

	@Override
	public boolean containsKey(K key, M modifier) {
		MEntry<V, M> entry = this.map.get(key);
		if (entry == null || entry.getModifier() == null)
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
			if (me.getModifier() != null && me.getModifier().equals(modifier) && me.getValue().equals(value))
				return true;
		return false;
	}

	@Override
	public void forEach(BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, M>> entry : this.map.entrySet())
			action.accept(entry.getKey(), MEntry.getValue(entry.getValue()));
	}

	@Override
	public void forEach(M modifier, BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, M>> entry : this.map.entrySet())
			if (MEntry.getModifier(entry.getValue()).equals(modifier))
				action.accept(entry.getKey(), MEntry.getValue(entry.getValue()));
	}

	@Override
	public void forEach(TriConsumer<K, V, M> action) {
		for (Entry<K, MEntry<V, M>> entry : this.map.entrySet())
			action.accept(entry.getKey(), MEntry.getValue(entry.getValue()), MEntry.getModifier(entry.getValue()));
	}

	@Override
	public Set<K> keySet() {
		return this.map.keySet();
	}

	@Data
	protected static class MEntry<V, M> {
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

		public static <V, M> V getValue(MEntry<V, M> entry) {
			return entry == null ? null : entry.getValue();
		}
		public static <V, M> M getModifier(MEntry<V, M> entry) {
			return entry == null ? null : entry.getModifier();
		}
	}

}
