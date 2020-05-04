package net.runeduniverse.libs.rogm.patterns;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public abstract class AParameterHolder {

	@Getter
	protected Class<?> clazz;
	@Getter
	protected String label;

	@Getter
	@Setter
	protected IdFieldPattern id;

	protected Map<String, FieldPattern> variables = new HashMap<String, FieldPattern>();

	public AParameterHolder(Class<?> clazz, String label) {
		this.clazz = clazz;
		if (label.trim() == "")
			this.label = clazz.getSimpleName();
		else
			this.label = label;
	}

	public FieldPattern getVariable(String tag) {
		return variables.get(tag);
	}

	public void addField(String tag, Field field) {
		this.addField(tag, new FieldPattern(field));
	}

	public void addField(String tag, FieldPattern pattern) {
		this.variables.put(tag, pattern);
	}
}
