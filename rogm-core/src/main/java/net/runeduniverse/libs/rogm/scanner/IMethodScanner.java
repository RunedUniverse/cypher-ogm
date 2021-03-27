package net.runeduniverse.libs.rogm.scanner;

import java.lang.reflect.Method;

public interface IMethodScanner {
	void scan(Method method, Class<?> type, TypePattern pattern);

}
