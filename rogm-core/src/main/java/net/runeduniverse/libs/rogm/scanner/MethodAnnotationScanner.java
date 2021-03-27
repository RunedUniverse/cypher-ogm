package net.runeduniverse.libs.rogm.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.runeduniverse.libs.rogm.annotations.Id;

public class MethodAnnotationScanner extends MethodScanner {

	private final Class<? extends Annotation> anno;

	public MethodAnnotationScanner(Class<? extends Annotation> anno) {
		super();
		this.anno = anno;
	}

	public MethodAnnotationScanner(Class<? extends Annotation> anno, ScanOrder order) {
		super(order);
		this.anno = anno;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void scan(Method method, Class<?> type, TypePattern pattern) {
		switch (this.order) {
		case FIRST:
			if (pattern.hasMethods(this.anno))
				return;
		case LAST:
			pattern.getFields().remove(Id.class);
		case ALL:
			if (type.isAnnotationPresent(this.anno))
				super.scan(method, type, pattern);
		}

	}

}
