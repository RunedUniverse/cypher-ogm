package net.runeduniverse.libs.rogm.parser;

import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;

@SuppressWarnings("deprecation")
public class JSONParser implements Parser{

	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	static {
		MAPPER.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		
		JsonAnnotationIntrospector introspector = new JsonAnnotationIntrospector();
		
		AnnotationIntrospector serial = new AnnotationIntrospectorPair(introspector, MAPPER.getSerializationConfig().getAnnotationIntrospector());
		AnnotationIntrospector deserial = new AnnotationIntrospectorPair(introspector, MAPPER.getDeserializationConfig().getAnnotationIntrospector());
		
		MAPPER.setAnnotationIntrospectors(serial, deserial);
	}

	
	@Override
	public String serialize(Object object) throws JsonProcessingException {
		return MAPPER.writeValueAsString(object);
	}

	@Override
	public String serialize(Map<String, Object> map) throws Exception {
		return MAPPER.writeValueAsString(map);
	}

	@Override
	public <T> T deserialize(Class<T> clazz, String value) throws JsonMappingException, JsonProcessingException {
		return MAPPER.readValue(value, clazz);
	}

}
