package net.runeduniverse.libs.rogm.scanner;

import java.lang.reflect.Field;

public interface IFieldScanner<F extends FieldPattern> {
	void scan(Field field, Class<?> type, TypePattern<F, ?> pattern);
}
