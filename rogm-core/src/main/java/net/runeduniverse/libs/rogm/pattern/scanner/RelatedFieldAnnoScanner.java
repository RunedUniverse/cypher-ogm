package net.runeduniverse.libs.rogm.pattern.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.runeduniverse.libs.rogm.pattern.FieldPattern;
import net.runeduniverse.libs.rogm.pattern.IStorage;
import net.runeduniverse.libs.rogm.pattern.RelatedFieldPattern;
import net.runeduniverse.libs.rogm.scanner.FieldScanner;
import net.runeduniverse.libs.rogm.scanner.ScanOrder;

public class RelatedFieldAnnoScanner extends FieldAnnoScanner {
	
	public RelatedFieldAnnoScanner(IStorage factory, Class<? extends Annotation> anno) {
		super(creator(factory), anno);
	}

	public RelatedFieldAnnoScanner(IStorage factory, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator(factory), anno, order);
	}

	private static FieldScanner.PatternCreator<FieldPattern> creator(IStorage factory){
		return new PatternCreator<FieldPattern>() {
			
			@Override
			public RelatedFieldPattern createPattern(Field field) {
				try {
					return new RelatedFieldPattern(factory, field);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}
	
}
