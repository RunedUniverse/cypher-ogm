package net.runeduniverse.libs.rogm.parser;

public interface Parser {
	
	String serialize(Object object) throws Exception;
	<T> T deserialize(Class<T> clazz, String value) throws Exception;

}
