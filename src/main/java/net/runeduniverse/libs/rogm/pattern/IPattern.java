package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;

import net.runeduniverse.libs.rogm.querying.IFilter;

public interface IPattern {
	IFilter createFilter() throws Exception;

	Object setId(Object object, Serializable id) throws IllegalArgumentException;

	Object parse(Serializable id, String data) throws Exception;
}
