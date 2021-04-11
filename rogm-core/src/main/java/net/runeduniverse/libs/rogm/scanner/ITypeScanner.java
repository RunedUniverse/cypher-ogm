package net.runeduniverse.libs.rogm.scanner;

public interface ITypeScanner {
	void scan(Class<?> type, ClassLoader loader, String pkg) throws Exception;
}
