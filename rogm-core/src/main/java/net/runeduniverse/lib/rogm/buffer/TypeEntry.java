package net.runeduniverse.lib.rogm.buffer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TypeEntry {

	protected Map<Serializable, Entry> idMap = new HashMap<>();
	protected Map<Serializable, Entry> entityIdMap = new HashMap<>();

	public Entry getIdEntry(Serializable id) {
		return this.idMap.get(id);
	}

	public Entry getEntityIdEntry(Serializable id) {
		return this.entityIdMap.get(id);
	}
}