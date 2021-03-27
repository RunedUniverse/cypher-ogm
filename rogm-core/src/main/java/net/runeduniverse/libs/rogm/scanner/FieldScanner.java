package net.runeduniverse.libs.rogm.scanner;

import java.lang.reflect.Field;

public class FieldScanner implements IFieldScanner {

	protected final ScanOrder order;

	public FieldScanner() {
		this.order = ScanOrder.ALL;
	}

	public FieldScanner(ScanOrder order) {
		this.order = order;
	}

	protected FieldPattern createPattern(Field field) {
		return new FieldPattern(field);
	}

	@Override
	public void scan(Field field, Class<?> type, TypePattern pattern) {
		FieldPattern p = createPattern(field);
		if (p != null)
			pattern.getFields().put(null, p);
	}

}
