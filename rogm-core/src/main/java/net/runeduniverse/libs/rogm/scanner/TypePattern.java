package net.runeduniverse.libs.rogm.scanner;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class TypePattern {

	@Getter
	private final Map<Class<? extends Annotation>, FieldPattern> fields = new HashMap<>();
	@Getter
	private final Map<Class<? extends Annotation>, MethodPattern> methods = new HashMap<>();

	@Getter
	protected final String pkg;
	@Getter
	protected final ClassLoader loader;
	@Getter
	protected final Class<?> type;
	@Getter
	protected final Class<?> superType;

	public TypePattern(String pkg, ClassLoader loader, Class<?> type) {
		this.pkg = pkg;
		this.loader = loader;
		this.type = type;
		this.superType = type.getSuperclass();
	}

	@SuppressWarnings("unchecked")
	public boolean hasFields(Class<? extends Annotation>... annos) {
		for (Class<? extends Annotation> anno : annos)
			if (!this.fields.containsKey(anno))
				return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean hasMethods(Class<? extends Annotation>... annos) {
		for (Class<? extends Annotation> anno : annos)
			if (!this.methods.containsKey(anno))
				return false;
		return true;
	}
}
