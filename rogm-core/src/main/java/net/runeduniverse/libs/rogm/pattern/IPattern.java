package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.buffer.IBuffer.Entry;
import net.runeduniverse.libs.rogm.buffer.IBuffer.LoadState;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface IPattern {
	PatternType getPatternType();

	boolean isIdSet(Object entity);

	Serializable getId(Object entity);

	Class<?> getType();

	Collection<String> getLabels();

	FieldPattern getField(Class<? extends Annotation> anno);

	IConverter<?> getIdConverter();

	Object setId(Object entity, Serializable id) throws IllegalArgumentException;

	Serializable prepareEntityId(Serializable id, Serializable entityId);

	Object parse(IData data, LoadState loadState, Set<Entry> lazyEntries) throws Exception;

	Entry update(IData data) throws Exception;

	/**
	 * Used to call parsed Methods
	 * 
	 * @param anno
	 * @param obj
	 * @return {@code true} if successfull
	 */
	public boolean callMethod(Class<? extends Annotation> anno, Object obj, Object... args);

	public static enum PatternType {
		NODE, RELATION, ADAPTER, UNKNOWN
	}

	public interface IPatternContainer extends IFilter {
		IPattern getPattern();

		public static boolean identify(IFilter filter) {
			return filter instanceof IPatternContainer && ((IPatternContainer) filter).getPattern() != null;
		}
	}

	public interface IData {
		Serializable getId();

		Serializable getEntityId();

		void setEntityId(Serializable entityId);

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
		IDataContainer getDataContainer() throws Exception;

		Set<IFilter> getRelatedFilter() throws Exception;

		void postSave();
	}

	public interface IDeleteContainer {
		IFRelation getEffectedFilter();

		IFilter getDeleteFilter();

		Serializable getDeletedId();
	}
}
