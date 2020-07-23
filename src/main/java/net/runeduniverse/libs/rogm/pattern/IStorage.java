package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.Collection;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDataRecord;
import net.runeduniverse.libs.rogm.pattern.IPattern.ISaveContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface IStorage {

	Configuration getConfig();

	Parser.Instance getParser();

	IBuffer getBuffer();

	IPattern getPattern(Class<?> clazz) throws Exception;

	IFilter createFilter(Class<?> clazz) throws Exception;

	ISaveContainer createFilter(Object entity) throws Exception;

	IFilter createIdFilter(Class<?> clazz, Serializable id) throws Exception;

	<T> Collection<T> parse(Class<T> type, IDataRecord record) throws Exception;
}
