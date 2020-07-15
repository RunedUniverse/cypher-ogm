package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.runeduniverse.libs.rogm.lang.Language.DataFilter;
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
	DataFilter createFilter(Object entity) throws Exception;

	Object setId(Object entity, Serializable id) throws IllegalArgumentException;

	Object parse(Serializable id, String data) throws Exception;
	
	Object parse(Data data) throws Exception;
	
	public interface IPatternContainer extends IFilter{
		IPattern getPattern();
	
		public static boolean identify(IFilter filter) {
			return filter instanceof IPatternContainer && ((IPatternContainer) filter).getPattern() != null;
		}
	}
	
	public interface Data{
		Serializable getId();
		Set<String> getLabels();
		String getData();
		IFilter getFilter();
	}
	
	public interface DataRecord{
		IPatternContainer getPrimaryFilter();
		Map<Serializable, List<Data>> getData();
	}
}
