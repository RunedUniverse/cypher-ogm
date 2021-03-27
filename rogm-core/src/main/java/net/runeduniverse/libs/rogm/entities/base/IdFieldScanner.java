package net.runeduniverse.libs.rogm.entities.base;

import java.lang.reflect.Field;

import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.entities.Pattern;
import net.runeduniverse.libs.rogm.scanner.FieldAnnotationScanner;
import net.runeduniverse.libs.rogm.scanner.FieldPattern;
import net.runeduniverse.libs.rogm.scanner.ScanOrder;
import net.runeduniverse.libs.rogm.scanner.TypePattern;

public class IdFieldScanner extends FieldAnnotationScanner{

	public IdFieldScanner() {
		super(Id.class, ScanOrder.FIRST);
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
			if (p != null && type.isAnnotationPresent(this.anno)) {
				pattern.getFields().put(this.anno, createPattern(field));
				try {
					((Pattern)pattern).setIdConverter(IConverter.createConverter(field.getAnnotation(Id.class), field.getType()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
