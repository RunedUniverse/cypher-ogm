/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.libs.rogm.parser.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.parser.Parser.Instance;

@RequiredArgsConstructor
class JSONParserInstance implements Instance {
	private final ObjectMapper mapper;
	private final boolean serializeNullAsEmptyObject;

	@Override
	public String serialize(Object object) throws JsonProcessingException {
		if (object == null && this.serializeNullAsEmptyObject)
			return "{}";
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