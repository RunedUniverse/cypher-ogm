package net.runeduniverse.libs.rogm.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.runeduniverse.libs.rogm.modules.Module.IRawRecord;

public class RawRecord implements IRawRecord {

	private final List<Map<String, Object>> data;

	public RawRecord() {
		this.data = new ArrayList<>();
	}

	public RawRecord(List<Map<String, Object>> data) {
		this.data = data;
	}

	@Override
	public List<Map<String, Object>> getRawData() {
		return this.data;
	}

	public void addEntry(Map<String, Object> entry) {
		this.data.add(entry);
	}

}