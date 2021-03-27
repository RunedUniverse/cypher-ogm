package net.runeduniverse.libs.rogm.scanner;

import java.lang.annotation.Annotation;

public class TypeAnnotationScanner extends TypeScanner {

	private final Class<? extends Annotation> anno;

	public TypeAnnotationScanner(Class<? extends Annotation> anno, ResultConsumer consumer) {
		super(consumer);
		this.anno = anno;
	}

	@Override
	public void scan(Class<?> type, ClassLoader loader, String pkg) {
		if (type.isAnnotationPresent(this.anno))
			super.scan(type, loader, pkg);
	}
}
