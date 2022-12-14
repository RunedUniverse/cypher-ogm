package net.runeduniverse.lib.rogm.api.modules;

import java.util.List;
import java.util.Map;

public interface IRawDataRecord {
	// returns a Map with the ALIAS as Key and DATA as Value
	List<Map<String, Data>> getData();
}