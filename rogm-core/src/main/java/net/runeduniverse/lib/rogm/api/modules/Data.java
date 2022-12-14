package net.runeduniverse.lib.rogm.api.modules;

import java.io.Serializable;
import java.util.Set;

public interface Data {
	Serializable getId();

	String getEntityId();

	Set<String> getLabels();

	String getData();

	String getAlias();
}