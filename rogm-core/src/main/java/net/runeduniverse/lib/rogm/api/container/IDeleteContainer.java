package net.runeduniverse.lib.rogm.api.container;

import java.io.Serializable;

import net.runeduniverse.lib.rogm.api.querying.IFRelation;
import net.runeduniverse.lib.rogm.api.querying.IFilter;

public interface IDeleteContainer {
	IFRelation getEffectedFilter();

	IFilter getDeleteFilter();

	Serializable getDeletedId();
}