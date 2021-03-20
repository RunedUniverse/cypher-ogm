package net.runeduniverse.libs.rogm.entities.base;

import java.lang.reflect.Modifier;

import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.entities.APatternScanner;

public class RelationPatternScanner extends APatternScanner {

	@Override
	public void scan(Class<?> clazz, ClassLoader loader, String pkg) {
		if (!clazz.isAnnotationPresent(RelationshipEntity.class) || Modifier.isAbstract(clazz.getModifiers()))
			return;
	}

}
