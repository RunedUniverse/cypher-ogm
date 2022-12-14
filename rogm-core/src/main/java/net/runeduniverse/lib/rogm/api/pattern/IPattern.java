/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.lib.rogm.api.pattern;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface IPattern {
	PatternType getPatternType();

	Class<?> getType();

	Collection<String> getLabels();

	IFieldPattern getField(Class<? extends Annotation> anno);

	/**
	 * Used to call parsed Methods
	 * 
	 * @param anno	{@link Annotation} by which the method can be identified
	 * @param obj   {@link Object} which has the method
	 * @param args  {@link Object} array which gets passed to the method
	 * @return {@code true} if successfull
	 */
	public boolean callMethod(Class<? extends Annotation> anno, Object obj, Object... args);
}
