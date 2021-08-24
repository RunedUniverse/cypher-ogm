package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface IPattern {
	PatternType getPatternType();

	Class<?> getType();

	Collection<String> getLabels();

	FieldPattern getField(Class<? extends Annotation> anno);

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

	public interface IData extends InternalBufferTypes {
		Serializable getId();

		Serializable getEntityId();

		void setEntityId(Serializable entityId);

		Set<String> getLabels();

		String getData();

		IFilter getFilter();

		default LoadState getLoadState() {
			return LoadState.get(this.getFilter());
		}
	}

	public interface IDataRecord {
		IPatternContainer getPrimaryFilter();

		Set<Serializable> getIds();

		List<Set<IData>> getData();
	}

	public interface IDeleteContainer {
		IFRelation getEffectedFilter();

		IFilter getDeleteFilter();

		Serializable getDeletedId();
	}
}
