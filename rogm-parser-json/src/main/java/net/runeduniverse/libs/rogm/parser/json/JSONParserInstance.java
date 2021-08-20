package net.runeduniverse.libs.rogm.parser.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.parser.Parser.Instance;

@RequiredArgsConstructor
class JSONParserInstance implements Instance {
	private final ObjectMapper mapper;

	@Override
	public String serialize(Object object) throws JsonProcessingException {
		return mapper.writeValueAsString(object);
	}

	@Override
	public <T> T deserialize(Class<T> clazz, String value)
			throws JsonMappingException, JsonProcessingException, InstantiationException, IllegalAccessException {
		if (value == null)
			return clazz.newInstance();
		return mapper.readValue(value, clazz);
	}

	@Override
	public <T> void deserialize(T obj, String value) throws JsonMappingException, JsonProcessingException {
		if (value == null)
			return;
		this.mapper.readerForUpdating(obj)
				.readValue(value);
	}
}