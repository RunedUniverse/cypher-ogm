package net.runeduniverse.libs.rogm.scanner;

import java.lang.reflect.Field;

public interface IFieldScanner {
	void scan(Field field, Class<?> type, TypePattern pattern);
}
