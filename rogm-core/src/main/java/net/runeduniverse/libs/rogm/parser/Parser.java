package net.runeduniverse.libs.rogm.parser;

import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.modules.IdTypeResolver;

public interface Parser {

	Instance build(final Logger logger, IdTypeResolver resolver);

	public interface Instance {
		String serialize(Object object) throws Exception;

		<T> T deserialize(Class<T> clazz, String value) throws Exception;

		<T> void deserialize(T obj, String value) throws Exception;
	}
}
