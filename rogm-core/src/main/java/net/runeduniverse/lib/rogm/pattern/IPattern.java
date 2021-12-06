/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.rogm.pattern;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.runeduniverse.lib.rogm.buffer.BufferTypes;
import net.runeduniverse.lib.rogm.querying.IFRelation;
import net.runeduniverse.lib.rogm.querying.IFilter;

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
		IBaseQueryPattern<?> getPattern();

		public static boolean identify(IFilter filter) {
			return filter instanceof IPatternContainer && ((IPatternContainer) filter).getPattern() != null;
		}
	}

	public interface IData extends BufferTypes {
		Serializable getId();

		Serializable getEntityId();

		void setEntityId(Serializable entityId);

		Set<String> getLabels();

		String getData();

		IFilter getFilter();

		default LoadState getLoadState() {
			return LoadState.get(this.getFilter());
		}

		default String valuesToString() {
			return "IData[" + this.hashCode() + "]\nid:        " + this.getId() + "\nentity_id: " + this.getEntityId()
					+ "\nlables:    [" + String.join(", ", this.getLabels()) + "]\ndata:      " + this.getData()
					+ "\nfilter:    " + this.getFilter();
		}
	}

	public interface IDataRecord {
		IFilter getPrimaryFilter();

		Set<Serializable> getIds();

		List<Set<IData>> getData();
	}

	public interface IDeleteContainer {
		IFRelation getEffectedFilter();

		IFilter getDeleteFilter();

		Serializable getDeletedId();
	}
}
