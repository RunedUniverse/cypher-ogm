package net.runeduniverse.lib.rogm.buffer;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runeduniverse.lib.rogm.api.buffer.IEntry;
import net.runeduniverse.lib.rogm.api.buffer.LoadState;
import net.runeduniverse.lib.rogm.api.pattern.IBaseQueryPattern;
import net.runeduniverse.lib.rogm.api.pattern.IData;

@Data
@AllArgsConstructor
public class Entry implements IEntry {

	private Serializable id;
	private Serializable entityId;
	private Object entity;
	private LoadState loadState;
	private Class<?> type;
	private IBaseQueryPattern<?> pattern;

	public Entry(IData data, Object entity, LoadState loadState, IBaseQueryPattern<?> pattern) {
		this.id = data.getId();
		this.entityId = data.getEntityId();
		this.entity = entity;
		this.loadState = loadState;
		this.type = entity.getClass();
		this.pattern = pattern;
	}

	public static Entry from(IEntry iEntry) {
		if (iEntry instanceof Entry)
			return (Entry) iEntry;
		return new Entry(iEntry.getId(), iEntry.getEntityId(), iEntry.getEntity(), iEntry.getLoadState(),
				iEntry.getType(), iEntry.getPattern());
	}
}