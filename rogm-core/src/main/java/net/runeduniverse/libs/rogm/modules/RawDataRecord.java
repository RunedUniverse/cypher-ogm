package net.runeduniverse.libs.rogm.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.runeduniverse.libs.rogm.modules.Module.Data;
import net.runeduniverse.libs.rogm.modules.Module.IRawDataRecord;

public class RawDataRecord implements IRawDataRecord {

	private final List<Map<String, Data>> data;

	public RawDataRecord() {
		this.data = new ArrayList<>();
	}

	public RawDataRecord(List<Map<String, Data>> data) {
		this.data = data;
	}

	@Override
	public List<Map<String, Data>> getData() {
		return this.data;
	}

	public void addEntry(Map<String, Data> entry) {
		this.data.add(entry);
	}

}