package net.runeduniverse.libs.rogm.pattern.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.runeduniverse.libs.rogm.pattern.FieldPattern;
import net.runeduniverse.libs.rogm.pattern.IStorage;
import net.runeduniverse.libs.rogm.scanner.FieldScanner;
import net.runeduniverse.libs.rogm.scanner.ScanOrder;

public class FieldAnnoScanner extends net.runeduniverse.libs.rogm.scanner.FieldAnnotationScanner<FieldPattern> {

	public FieldAnnoScanner(IStorage factory, Class<? extends Annotation> anno) {
		super(creator(factory), anno);
	}

	public FieldAnnoScanner(IStorage factory, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator(factory), anno, order);
	}

	public FieldAnnoScanner(FieldScanner.PatternCreator<FieldPattern> creator, Class<? extends Annotation> anno) {
		super(creator, anno);
	}

	public FieldAnnoScanner(FieldScanner.PatternCreator<FieldPattern> creator, Class<? extends Annotation> anno,
			ScanOrder order) {
		super(creator, anno, order);
	}

	private static FieldScanner.PatternCreator<FieldPattern> creator(IStorage factory) {
		return new PatternCreator<FieldPattern>() {

			@Override
			public FieldPattern createPattern(Field field) {
				try {
					return new FieldPattern(factory, field);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}
}
