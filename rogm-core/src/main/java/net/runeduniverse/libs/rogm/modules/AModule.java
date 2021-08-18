package net.runeduniverse.libs.rogm.modules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.scanner.TypeScanner;

public abstract class AModule implements Module {

	@Override
	public void configure(Archive archive) throws ScannerException {
		archive.scan(new TypeScanner.NodeScanner(archive, p -> archive.addEntry(p.getType(), p)),
				new TypeScanner.RelationScanner(archive, p -> archive.addEntry(p.getType(), p)));
	}

	protected static class RawRecord implements IRawRecord {

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

	protected static class RawDataRecord implements IRawDataRecord {

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

	protected static class RawIdRecord implements IRawIdRecord {
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
}
