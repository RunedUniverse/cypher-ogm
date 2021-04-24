package net.runeduniverse.libs.rogm.pattern.scanner;

import java.lang.annotation.Annotation;
import net.runeduniverse.libs.rogm.pattern.FieldPattern;
import net.runeduniverse.libs.rogm.pattern.IStorage;
import net.runeduniverse.libs.rogm.pattern.RelatedFieldPattern;
import net.runeduniverse.libs.scanner.FieldScanner;
import net.runeduniverse.libs.scanner.ScanOrder;

public class RelatedFieldAnnoScanner extends FieldAnnoScanner {
	
	public RelatedFieldAnnoScanner(IStorage factory, Class<? extends Annotation> anno) {
		super(creator(factory), anno);
	}

	public RelatedFieldAnnoScanner(IStorage factory, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator(factory), anno, order);
	}

	private static FieldScanner.PatternCreator<FieldPattern> creator(IStorage factory){
		return f -> new RelatedFieldPattern(factory, f);
	}
	
}
