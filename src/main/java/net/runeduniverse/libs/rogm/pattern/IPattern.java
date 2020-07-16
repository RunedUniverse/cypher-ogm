package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import net.runeduniverse.libs.rogm.lang.Language.IDataFilter;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.util.Buffer;

public interface IPattern {
	boolean isIdSet(Object entity);

	Serializable getId(Object entity);

	Buffer getBuffer();

	Class<?> getType();

	// querry
	IFilter createFilter() throws Exception;

	// querry exactly 1 node / querry deeper layers for node
	IFilter createIdFilter(Serializable id) throws Exception;

	// for saving
	ISaveContainer createFilter(Object entity) throws Exception;

	Object setId(Object entity, Serializable id) throws IllegalArgumentException;

	Object parse(Serializable id, String data) throws Exception;

	Object parse(IData data) throws Exception;

	void preSave(Object entity);

	void preDelete(Object entity);

	void postLoad(Object entity);

	void postSave(Object entity);

	void postDelete(Object entity);

	public interface IPatternContainer extends IFilter {
		IPattern getPattern();

		public static boolean identify(IFilter filter) {
			return filter instanceof IPatternContainer && ((IPatternContainer) filter).getPattern() != null;
		}
	}

	public interface IData {
		Serializable getId();

		Set<String> getLabels();

		String getData();

		IFilter getFilter();
	}

	public interface IDataRecord {
		IPatternContainer getPrimaryFilter();

		Set<Serializable> getIds();

		List<Set<IData>> getData();
	}

	public interface ISaveContainer {
		IDataFilter getDataFilter() throws Exception;

		void postSave();
	}
}
