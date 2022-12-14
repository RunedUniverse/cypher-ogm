package net.runeduniverse.lib.rogm.api.modules;

import java.util.List;
import java.util.Map;

public interface IRawRecord {
	// return the raw data
	List<Map<String, Object>> getRawData();
}