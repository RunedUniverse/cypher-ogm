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

public enum Feature {

	// PARSER FEATURES
	SERIALIZE_NULL_AS_EMPTY_OBJECT(false),

	// SERIALIZER FEATURES
	SERIALIZER_QUOTE_FIELD_NAMES(true),

	// DESERIALIZER FEATURES
	DESERIALIZER_ALLOW_UNQUOTED_FIELD_NAMES(false), DESERIALIZER_FAIL_ON_UNKNOWN_PROPERTIES(false),

	// MAPPER FEATURES
	MAPPER_AUTO_DETECT_GETTERS(true), MAPPER_AUTO_DETECT_IS_GETTERS(true), MAPPER_AUTO_DETECT_SETTERS(true);

	private final boolean defaultValue;

	private Feature(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean getDefaultValue() {
		return this.defaultValue;
	}
}
