package net.runeduniverse.libs.rogm.util;

import java.util.function.BiConsumer;

public interface ModifiableMap<K, V, M> {
	V put(K key, V value);

	V put(K key, V value, M modifier);

	V get(K key);

	void forEach(BiConsumer<K, V> action);

	void forEach(M modifier, BiConsumer<K, V> action);
}
