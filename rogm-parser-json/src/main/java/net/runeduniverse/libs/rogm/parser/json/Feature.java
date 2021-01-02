package net.runeduniverse.libs.rogm.parser.json;

public enum Feature {

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
