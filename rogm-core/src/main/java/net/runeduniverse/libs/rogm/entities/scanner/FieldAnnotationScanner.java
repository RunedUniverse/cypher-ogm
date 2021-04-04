package net.runeduniverse.libs.rogm.entities.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.runeduniverse.libs.rogm.entities.FieldPattern;
import net.runeduniverse.libs.rogm.scanner.ScanOrder;

public class FieldAnnotationScanner extends net.runeduniverse.libs.rogm.scanner.FieldAnnotationScanner<FieldPattern> {
	public FieldAnnotationScanner(Class<? extends Annotation> anno) {
		super(FieldAnnotationScanner::createPattern, anno);
	}

	public FieldAnnotationScanner(Class<? extends Annotation> anno, ScanOrder order) {
		super(FieldAnnotationScanner::createPattern, anno, order);
	}

	protected static FieldPattern createPattern(Field field) {
		try {
			return new FieldPattern(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
