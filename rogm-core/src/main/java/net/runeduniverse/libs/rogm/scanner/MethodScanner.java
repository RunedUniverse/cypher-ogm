package net.runeduniverse.libs.rogm.scanner;

import java.lang.reflect.Method;

public class MethodScanner implements IMethodScanner {

	protected final ScanOrder order;

	public MethodScanner() {
		this.order = ScanOrder.ALL;
	}

	public MethodScanner(ScanOrder order) {
		this.order = order;
	}

	@Override
	public void scan(Method method, Class<?> type, TypePattern pattern) {
		pattern.getMethods().put(null, new MethodPattern(method));
	}

}
