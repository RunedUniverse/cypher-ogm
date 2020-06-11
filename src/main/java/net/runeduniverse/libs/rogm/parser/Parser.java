package net.runeduniverse.libs.rogm.parser;

import java.util.Map;

import net.runeduniverse.libs.rogm.querying.ParamFilter;

public interface Parser {
	
	String serialize(ParamFilter filter) throws Exception;
	String serialize(Map<String, Object> map) throws Exception;
	String serialize(Object object) throws Exception;
	<T> T deserialize(Class<T> clazz, String value) throws Exception;

}
