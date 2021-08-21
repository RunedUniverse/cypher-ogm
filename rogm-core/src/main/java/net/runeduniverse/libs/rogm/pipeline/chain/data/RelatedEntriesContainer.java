package net.runeduniverse.libs.rogm.pipeline.chain.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.Entry;

@NoArgsConstructor
public class RelatedEntriesContainer {
	@Getter
	private final Set<Entry> lazyEntries = new HashSet<>();

	public boolean addEntry(Entry entry) {
		return this.lazyEntries.add(entry);
	}

	public boolean addAllEntries(Collection<Entry> entries) {
		return this.lazyEntries.addAll(entries);
	}

	public boolean isEmpty() {
		return this.lazyEntries.isEmpty();
	}

	public void clear() {
		this.lazyEntries.clear();
	}
}
