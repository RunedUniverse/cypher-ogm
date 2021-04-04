package net.runeduniverse.libs.rogm.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.runeduniverse.libs.rogm.annotations.Id;

public class FieldAnnotationScanner<F extends FieldPattern> extends FieldScanner<F> {

	protected final Class<? extends Annotation> anno;

	public FieldAnnotationScanner(PatternCreator<F> creator, Class<? extends Annotation> anno) {
		super(creator);
		this.anno = anno;
	}

	public FieldAnnotationScanner(PatternCreator<F> creator, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator, order);
		this.anno = anno;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void scan(Field field, Class<?> type, TypePattern<F, ?> pattern) {
		switch (this.order) {
		case FIRST:
			if (pattern.hasFields(this.anno))
				return;
		case LAST:
			pattern.getFields()
					.remove(Id.class);
		case ALL:
			F p = this.creator.createPattern(field);
			if (p != null && type.isAnnotationPresent(this.anno))
				pattern.getFields()
						.put(this.anno, p);
		}
	}

	public static FieldAnnotationScanner<FieldPattern> DEFAULT(Class<? extends Annotation> anno) {
		return new FieldAnnotationScanner<FieldPattern>(FieldScanner::createPattern, anno);
	}

	public static FieldAnnotationScanner<FieldPattern> DEFAULT(Class<? extends Annotation> anno, ScanOrder order) {
		return new FieldAnnotationScanner<FieldPattern>(FieldScanner::createPattern, anno, order);
	}

}
