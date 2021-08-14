package net.runeduniverse.libs.rogm.modules;

public interface IdTypeResolver {
	Class<?> idType();

	boolean checkIdType(Class<?> type);

	String getIdAlias();
}
