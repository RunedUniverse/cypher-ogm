package net.runeduniverse.libs.rogm.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface FieldAccessor {
	public <T extends Object, ID extends Serializable> T setObjectId(T obj, ID id);

	public <ANNO extends Annotation> Field findAnnotatedField(Class<?> clazz, Class<ANNO> anno);
}
