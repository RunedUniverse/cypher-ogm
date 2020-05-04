package net.runeduniverse.libs.rogm.patterns;

import java.lang.reflect.Field;

import lombok.Getter;
import lombok.Setter;

public class IdFieldPattern extends FieldPattern {

	public IdFieldPattern(Field field) {
		super(field);
	}

	@Getter
	@Setter
	private boolean isGenerated = false;

}
