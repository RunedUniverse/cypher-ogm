package net.runeduniverse.libs.rogm.patterns;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
		if (label.isEmpty())
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Class:        " + this.clazz.getName() + "\nLabel:        " + this.label + "\nID:           "
				+ (this.id == null ? "null" : this.id.getType()) + "\nVars:");
		for (Entry<String, FieldPattern> entry : this.variables.entrySet())
			builder.append('\n' + entry.getKey() + "||" + entry.getValue().getType());
		return builder.toString();
	}
}
