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
package net.runeduniverse.lib.rogm.api.container;

import java.util.Map;
import java.util.Set;

import net.runeduniverse.lib.rogm.api.buffer.IBuffer;
import net.runeduniverse.lib.rogm.api.pattern.IArchive;
import net.runeduniverse.lib.rogm.api.querying.IDataContainer;
import net.runeduniverse.lib.rogm.api.querying.IFilter;
import net.runeduniverse.lib.rogm.api.querying.IQueryBuilderInstance;
import net.runeduniverse.lib.utils.errors.ExceptionSuppressions;

public interface ISaveContainer {
	public Map<Object, IQueryBuilderInstance<?, ?, ? extends IFilter>> getIncludedData();

	public IDataContainer getDataContainer();

	public Set<IFilter> calculateEffectedFilter(final IArchive archive, final IBuffer buffer) throws Exception;

	public void postSave(final IArchive archive) throws ExceptionSuppressions;

	public void setDataContainer(IDataContainer container);

	public void setCalculator(EffectedFilterCalculator calculator);

	@FunctionalInterface
	public static interface DataContainerCreator {
		IDataContainer create(final Map<Object, IQueryBuilderInstance<?, ?, ? extends IFilter>> includedData)
				throws Exception;
	}

	@FunctionalInterface
	public static interface EffectedFilterCalculator {
		Set<IFilter> calculate(final IArchive archive, final IBuffer buffer,
				final Map<Object, IQueryBuilderInstance<?, ?, ? extends IFilter>> includedData) throws Exception;
	}
}
