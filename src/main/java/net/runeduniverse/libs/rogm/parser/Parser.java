package net.runeduniverse.libs.rogm.parser;

import net.runeduniverse.libs.rogm.querying.ParamFilter;

public interface Parser {
	
	String serialize(Object object) throws Exception;
	<T> T deserialize(Class<T> clazz, String value) throws Exception;

	String serialize(ParamFilter filter) throws Exception;
}
