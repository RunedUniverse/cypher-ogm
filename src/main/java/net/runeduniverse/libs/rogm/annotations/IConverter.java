package net.runeduniverse.libs.rogm.annotations;

import java.io.Serializable;
import java.util.UUID;

public interface IConverter<T extends Serializable> {

	T convert(String serial);

	String convert(T id);

	public static IConverter<?> createConverter(Id anno, Class<?> type) throws Exception {
		if (anno.converter() != UnSet.class)
			return (IConverter<?>) anno.converter().newInstance();

		switch (type.getName()) {
		case "java.lang.Short":
			return new ShortConverter();
		case "java.lang.Integer":
			return new IntegerConverter();
		case "java.lang.Long":
			return new LongConverter();
		case "java.util.UUID":
			return new UUIDConverter();
		default:
			throw new Exception("No idConverter for Class<" + type + "> defined!");
		}

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