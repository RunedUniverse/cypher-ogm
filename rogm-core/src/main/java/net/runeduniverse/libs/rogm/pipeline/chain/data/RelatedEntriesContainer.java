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
	private final Set<Entry> relatedEntries = new HashSet<>();

	public boolean addEntry(Entry entry) {
		return this.relatedEntries.add(entry);
	}

	public boolean addAllEntries(Collection<Entry> entries) {
		return this.relatedEntries.addAll(entries);
	}

	public boolean addEntries(RelatedEntriesContainer container) {
		return this.relatedEntries.addAll(container.getRelatedEntries());
	}

	public boolean isEmpty() {
		return this.relatedEntries.isEmpty();
	}

	public void clear() {
		this.relatedEntries.clear();
	}
}
