package net.runeduniverse.libs.rogm.entities;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.scanner.TypePattern;

public class Pattern extends TypePattern implements IPattern{
	
	@Getter @Setter
	protected IConverter<?> idConverter = null;

	public Pattern(String pkg, ClassLoader loader, Class<?> type) {
		super(pkg, loader, type);
	}

}
