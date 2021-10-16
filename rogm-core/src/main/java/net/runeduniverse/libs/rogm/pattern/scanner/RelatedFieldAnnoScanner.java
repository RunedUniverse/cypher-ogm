package net.runeduniverse.libs.rogm.pattern.scanner;

import java.lang.annotation.Annotation;

import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.FieldPattern;
import net.runeduniverse.libs.rogm.pattern.RelatedFieldPattern;
import net.runeduniverse.libs.scanner.FieldScanner;
import net.runeduniverse.libs.scanner.ScanOrder;

public class RelatedFieldAnnoScanner extends FieldAnnoScanner {
	
	public RelatedFieldAnnoScanner(Archive archive, Class<? extends Annotation> anno) {
		super(creator(archive), anno);
	}

	public RelatedFieldAnnoScanner(Archive archive, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator(archive), anno, order);
	}

	private static FieldScanner.PatternCreator<FieldPattern> creator(Archive archive){
		return f -> new RelatedFieldPattern(archive, f);
	}
	
}
