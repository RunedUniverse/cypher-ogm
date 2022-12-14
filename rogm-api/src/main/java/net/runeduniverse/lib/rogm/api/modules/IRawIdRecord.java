package net.runeduniverse.lib.rogm.api.modules;

import java.io.Serializable;
import java.util.Map;

public interface IRawIdRecord {
	// returns a Map with the ALIAS and the IDs
	Map<String, Serializable> getIds();
}