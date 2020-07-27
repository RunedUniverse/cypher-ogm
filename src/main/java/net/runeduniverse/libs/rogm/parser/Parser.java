package net.runeduniverse.libs.rogm.parser;

import net.runeduniverse.libs.rogm.Configuration;

public interface Parser {

	Instance build(Configuration cnf);

	public interface Instance {
		String serialize(Object object) throws Exception;

		<T> T deserialize(Class<T> clazz, String value) throws Exception;
	}
}
