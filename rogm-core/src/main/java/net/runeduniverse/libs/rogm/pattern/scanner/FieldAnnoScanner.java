package net.runeduniverse.libs.rogm.pattern.scanner;

import java.lang.annotation.Annotation;

import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.FieldPattern;
import net.runeduniverse.libs.scanner.FieldScanner;
import net.runeduniverse.libs.scanner.ScanOrder;

public class FieldAnnoScanner extends net.runeduniverse.libs.scanner.FieldAnnotationScanner<FieldPattern> {

	public FieldAnnoScanner(Archive archive, Class<? extends Annotation> anno) {
		super(creator(archive), anno);
	}

	public FieldAnnoScanner(Archive archive, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator(archive), anno, order);
	}

	public FieldAnnoScanner(FieldScanner.PatternCreator<FieldPattern> creator, Class<? extends Annotation> anno) {
		super(creator, anno);
	}

	public FieldAnnoScanner(FieldScanner.PatternCreator<FieldPattern> creator, Class<? extends Annotation> anno,
			ScanOrder order) {
		super(creator, anno, order);
	}

	private static FieldScanner.PatternCreator<FieldPattern> creator(Archive archive) {
		return f -> new FieldPattern(archive, f);
	}
}
