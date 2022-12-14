package net.runeduniverse.lib.rogm.api.buffer;

import java.io.Serializable;

import net.runeduniverse.lib.rogm.api.pattern.IBaseQueryPattern;

public interface IEntry {
	Serializable getId();

	Serializable getEntityId();

	Object getEntity();

	LoadState getLoadState();

	Class<?> getType();

	IBaseQueryPattern<?> getPattern();

	void setId(Serializable id);

	void setEntityId(Serializable entityId);

	void setEntity(Object entity);

	void setLoadState(LoadState state);

	void setType(Class<?> type);

	void setPattern(IBaseQueryPattern<?> pattern);
}