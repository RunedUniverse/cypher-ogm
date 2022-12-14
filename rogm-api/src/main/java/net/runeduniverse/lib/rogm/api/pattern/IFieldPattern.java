package net.runeduniverse.lib.rogm.api.pattern;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import net.runeduniverse.lib.rogm.api.annotations.IConverter;

public interface IFieldPattern {
	public Field getField();

	public Class<?> getType();

	public boolean isCollection();

	public IConverter<?> getConverter();

	public void setConverter(IConverter<?> converter);

	public void setValue(Object entity, Object value) throws IllegalArgumentException;

	public void putValue(Object entity, Object value) throws IllegalArgumentException;

	public Object getValue(Object entity) throws IllegalArgumentException;

	public void removeValues(Object entity, Collection<Object> deletedEntities);

	public void clearValue(Object entity);

	public <A extends Annotation> A getAnno(Class<A> annoType);
}
