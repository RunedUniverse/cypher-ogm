package net.runeduniverse.libs.rogm.annotations;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface IConverter<T extends Serializable> {

	static final Map<Class<?>, IConverter<?>> converter = new HashMap<>();

	T convert(String serial);

	String convert(T id);

	@SuppressWarnings("unchecked")
	default String toProperty(Serializable id) {
		return convert((T) id);
	}

	static IConverter<?> createConverter(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		IConverter<?> conv = converter.get(clazz);
		if (conv != null)
			return conv;

		conv = (IConverter<?>) clazz.newInstance();
		converter.put(clazz, conv);
		return conv;
	}

	public static IConverter<?> createConverter(Converter anno, Class<?> type) throws Exception {
		if (anno != null && anno.converter() != UnSet.class)
			return (IConverter<?>) anno.converter()
					.newInstance();

		switch (type.getName()) {
		case "java.lang.Short":
			return createConverter(ShortConverter.class);
		case "java.lang.Integer":
			return createConverter(IntegerConverter.class);
		case "java.lang.Long":
			return createConverter(LongConverter.class);
		case "java.util.UUID":
			return createConverter(UUIDConverter.class);
		case "java.lang.String":
			return createConverter(StringConverter.class);
		default:
			throw new Exception("No Converter for Class<" + type + "> defined!");
		}

	}

	public static IConverter<?> validate(IConverter<?> converter) {
		if (converter == null)
			return new UnSet();
		return converter;
	}

	public class UnSet implements IConverter<Serializable> {

		@Override
		public Serializable convert(String serial) {
			return null;
		}

		@Override
		public String convert(Serializable id) {
			return null;
		}

	}

	public class StringConverter implements IConverter<String> {

		@Override
		public String convert(String serial) {
			return serial;
		}

	}

	public class ShortConverter implements IConverter<Short> {

		@Override
		public Short convert(String serial) {
			return Short.parseShort(serial);
		}

		@Override
		public String convert(Short id) {
			return Short.toString(id);
		}

	}

	public class IntegerConverter implements IConverter<Integer> {

		@Override
		public Integer convert(String serial) {
			return Integer.parseInt(serial);
		}

		@Override
		public String convert(Integer id) {
			return Integer.toString(id);
		}

	}

	public class LongConverter implements IConverter<Long> {

		@Override
		public Long convert(String serial) {
			return Long.parseLong(serial);
		}

		@Override
		public String convert(Long id) {
			return Long.toString(id);
		}

	}

	public class UUIDConverter implements IConverter<UUID> {

		@Override
		public UUID convert(String serial) {
			return UUID.fromString(serial);
		}

		@Override
		public String convert(UUID id) {
			return id.toString();
		}

	}
}