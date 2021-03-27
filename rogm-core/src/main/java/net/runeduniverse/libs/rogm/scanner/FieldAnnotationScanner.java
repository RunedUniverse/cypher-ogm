package net.runeduniverse.libs.rogm.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.runeduniverse.libs.rogm.annotations.Id;

public class FieldAnnotationScanner extends FieldScanner {

	protected final Class<? extends Annotation> anno;

	public FieldAnnotationScanner(Class<? extends Annotation> anno) {
		super();
		this.anno = anno;
	}

	public FieldAnnotationScanner(Class<? extends Annotation> anno, ScanOrder order) {
		super(order);
		this.anno = anno;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void scan(Field field, Class<?> type, TypePattern pattern) {
		switch (this.order) {
		case FIRST:
			if (pattern.hasFields(this.anno))
				return;
		case LAST:
			pattern.getFields().remove(Id.class);
		case ALL:
			FieldPattern p = createPattern(field);
			if (p != null && type.isAnnotationPresent(this.anno))
				pattern.getFields().put(this.anno, createPattern(field));
		}

	}

}
