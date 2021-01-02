package net.runeduniverse.libs.rogm.parser.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.parser.Parser;

@SuppressWarnings("deprecation")
public class JSONParser implements Parser {
	private final ObjectMapper mapper = new ObjectMapper();

	public JSONParser() {
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setSerializationInclusion(Include.NON_NULL);
		this.resetFeatures();
	}

	public JSONParser configure(Feature feature, boolean value) {
		switch (feature) {
		case SERIALIZER_QUOTE_FIELD_NAMES:
			mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, value);
			break;
		case DESERIALIZER_ALLOW_UNQUOTED_FIELD_NAMES:
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, value);
			break;
		case DESERIALIZER_FAIL_ON_UNKNOWN_PROPERTIES:
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, value);
			break;
		case MAPPER_AUTO_DETECT_GETTERS:
			mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, value);
			break;
		case MAPPER_AUTO_DETECT_IS_GETTERS:
			mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, value);
			break;
		case MAPPER_AUTO_DETECT_SETTERS:
			mapper.configure(MapperFeature.AUTO_DETECT_SETTERS, value);
			break;
		}
		return this;
	}

	public void resetFeature(Feature feature) {
		switch (feature) {
		case SERIALIZER_QUOTE_FIELD_NAMES:
			mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, feature.getDefaultValue());
			break;
		case DESERIALIZER_ALLOW_UNQUOTED_FIELD_NAMES:
			mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, feature.getDefaultValue());
			break;
		case DESERIALIZER_FAIL_ON_UNKNOWN_PROPERTIES:
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, feature.getDefaultValue());
			break;
		case MAPPER_AUTO_DETECT_GETTERS:
			mapper.configure(MapperFeature.AUTO_DETECT_GETTERS, feature.getDefaultValue());
			break;
		case MAPPER_AUTO_DETECT_IS_GETTERS:
			mapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, feature.getDefaultValue());
			break;
		case MAPPER_AUTO_DETECT_SETTERS:
			mapper.configure(MapperFeature.AUTO_DETECT_SETTERS, feature.getDefaultValue());
			break;
		}
	}

	public void resetFeatures() {
		for (Feature feature : Feature.values())
			this.resetFeature(feature);
	}

	@Override
	public Instance build(Configuration cnf) {
		JsonAnnotationIntrospector introspector = new JsonAnnotationIntrospector(cnf.getModule());

		AnnotationIntrospector serial = new AnnotationIntrospectorPair(introspector,
				mapper.getSerializationConfig().getAnnotationIntrospector());
		AnnotationIntrospector deserial = new AnnotationIntrospectorPair(introspector,
				mapper.getDeserializationConfig().getAnnotationIntrospector());
		return new JSONParserInstance(mapper.copy().setAnnotationIntrospectors(serial, deserial));
	}

}
