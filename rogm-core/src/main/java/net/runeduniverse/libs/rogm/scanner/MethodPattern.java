package net.runeduniverse.libs.rogm.scanner;

import java.lang.reflect.Method;
import lombok.Data;

@Data
public class MethodPattern {
	private final Method method;

	public MethodPattern(Method method) {
		this.method = method;
		this.method.setAccessible(true);
	}
}
