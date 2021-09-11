package net.runeduniverse.libs.rogm.modules;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.runeduniverse.libs.rogm.modules.Module.IRawIdRecord;

public class RawIdRecord implements IRawIdRecord {
	private final Map<String, Serializable> data;

	public RawIdRecord() {
		this.data = new HashMap<>();
	}

	public RawIdRecord(Map<String, Serializable> data) {
		this.data = data;
	}

	@Override
	public Map<String, Serializable> getIds() {
		return this.data;
	}

	public Serializable put(String alias, Serializable id) {
		return this.data.put(alias, id);
	}
}