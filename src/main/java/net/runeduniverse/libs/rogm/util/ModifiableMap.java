package net.runeduniverse.libs.rogm.util;

import java.util.Set;
import java.util.function.BiConsumer;

public interface ModifiableMap<K, V, M> {
	V put(K key, V value);

	V put(K key, V value, M modifier);

	V get(K key);
	
	void setModifier(K key, M modifier);
	M getModifier(K key);
	
	boolean containsKey(K key);
	boolean containsKey(K key, M modifier);

	boolean containsValue(V value);
	boolean containsValue(V value, M modifier);
	
	void forEach(BiConsumer<K, V> action);

	void forEach(M modifier, BiConsumer<K, V> action);
	
	Set<K> keySet();
}
