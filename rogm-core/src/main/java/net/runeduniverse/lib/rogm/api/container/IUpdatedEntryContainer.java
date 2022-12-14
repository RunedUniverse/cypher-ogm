package net.runeduniverse.lib.rogm.api.container;

import java.io.Serializable;

import net.runeduniverse.lib.rogm.api.buffer.LoadState;

public interface IUpdatedEntryContainer {
	public Serializable getId();

	public Serializable getEntityId();

	public Object getEntity();

	public LoadState getLoadState();

	public void setId(Serializable id);

	public void setEntityId(Serializable getEntityId);

	public void setEntity(Object entity);

	public void setLoadState(LoadState loadState);
}
