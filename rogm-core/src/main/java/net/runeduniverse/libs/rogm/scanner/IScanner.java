package net.runeduniverse.libs.rogm.scanner;

public interface IScanner {
	void scan(Class<?> clazz, ClassLoader loader, String pkg);
}
