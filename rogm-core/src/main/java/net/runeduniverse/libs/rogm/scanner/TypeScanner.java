package net.runeduniverse.libs.rogm.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TypeScanner implements ITypeScanner {

	protected Set<IFieldScanner> fieldScanner = new HashSet<>();
	protected Set<IMethodScanner> methodScanner = new HashSet<>();
	protected final ResultConsumer consumer;

	public TypeScanner addFieldScanner(IFieldScanner fieldScanner) {
		this.fieldScanner.add(fieldScanner);
		return this;
	}

	public TypeScanner addFieldScanner(IMethodScanner methodScanner) {
		this.methodScanner.add(methodScanner);
		return this;
	}

	protected TypePattern createPattern(Class<?> type, ClassLoader loader, String pkg) {
		return new TypePattern(pkg, loader, type);
	}
	
	@Override
	public void scan(Class<?> type, ClassLoader loader, String pkg) {
		TypePattern pattern = createPattern(type, loader, pkg);
		TypeScanner.cascade(this, TypeScanner::scanFields, pattern, pattern.getClass());
		TypeScanner.cascade(this, TypeScanner::scanMethods, pattern, pattern.getClass());
		this.consumer.accept(pattern);
	}

	protected static void cascade(TypeScanner scanner, SCN scn, TypePattern pattern, Class<?> type) {
		scn.accept(scanner, pattern, type);
		if (type.getSuperclass().equals(Object.class))
			return;
		cascade(scanner, scn, pattern, type.getSuperclass());
	}

	protected static void scanFields(TypeScanner scanner, TypePattern pattern, Class<?> type) {
		for (Field f : pattern.getType().getDeclaredFields())
			for (IFieldScanner fscan : scanner.fieldScanner)
				fscan.scan(f, type, pattern);
	}

	protected static void scanMethods(TypeScanner scanner, TypePattern pattern, Class<?> type) {
		for (Method m : pattern.getType().getDeclaredMethods())
			for (IMethodScanner mscan : scanner.methodScanner)
				mscan.scan(m, type, pattern);
	}

	@FunctionalInterface
	protected static interface SCN {
		void accept(TypeScanner scanner, TypePattern pattern, Class<?> type);
	}
}
