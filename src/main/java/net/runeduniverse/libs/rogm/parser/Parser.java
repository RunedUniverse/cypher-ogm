package net.runeduniverse.libs.rogm.parser;

public interface Parser {
	
	String toJSON(Object object);
	<T> T fromJSON(Class<T> clazz, String json);

}
