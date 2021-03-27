package net.runeduniverse.libs.rogm.entities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.runeduniverse.libs.rogm.scanner.ScanOrder;

public class FieldAnnotationScanner extends net.runeduniverse.libs.rogm.scanner.FieldAnnotationScanner {
	public FieldAnnotationScanner(Class<? extends Annotation> anno) {
		super(anno);
	}
	public FieldAnnotationScanner(Class<? extends Annotation> anno, ScanOrder order) {
		super(anno, order);
	}
	
	@Override
	protected FieldPattern createPattern(Field field) {
		try {
			return new FieldPattern(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	

}
