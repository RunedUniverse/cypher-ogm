package net.runeduniverse.libs.rogm.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import net.runeduniverse.libs.rogm.querying.ParamFilter;

public class JSONParser implements Parser{

	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	static {
		AnnotationIntrospector serial = new AnnotationIntrospectorPair(new JsonAnnotationIntrospector(), MAPPER.getSerializationConfig().getAnnotationIntrospector());
		AnnotationIntrospector deserial = new AnnotationIntrospectorPair(new JsonAnnotationIntrospector(), MAPPER.getDeserializationConfig().getAnnotationIntrospector());
		
		MAPPER.setAnnotationIntrospectors(serial, deserial);
	}
	
	
	@Override
	public String serialize(Object object) throws JsonProcessingException {
		return MAPPER.writeValueAsString(object);
	}

	@Override
	public <T> T deserialize(Class<T> clazz, String value) throws JsonMappingException, JsonProcessingException {
		return MAPPER.readValue(value, clazz);
	}

	@Override
	public String serialize(ParamFilter filter) throws JsonProcessingException {
		return MAPPER.writeValueAsString(filter.getParams());
	}

}
