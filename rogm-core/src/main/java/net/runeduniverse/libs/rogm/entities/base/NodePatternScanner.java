package net.runeduniverse.libs.rogm.entities.base;

import java.lang.reflect.Modifier;

import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.entities.APatternScanner;

public class NodePatternScanner extends APatternScanner {

	@Override
	public void scan(Class<?> clazz, ClassLoader loader, String pkg) {
		if (!clazz.isAnnotationPresent(NodeEntity.class) || Modifier.isAbstract(clazz.getModifiers()))
			return;
	}

}
