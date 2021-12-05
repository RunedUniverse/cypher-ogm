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
package net.runeduniverse.libs.rogm.pipeline.chain.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.PostSave;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.errors.ExceptionSuppressions;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;

@ToString
public class SaveContainer {

	@Getter
	protected final Map<Object, IQueryBuilder<?, ?, ? extends IFilter>> includedData = new HashMap<>();

	protected IDataContainer container;
	protected EffectedFilterCalculator calculator = (a, b, i) -> new HashSet<>();

	public SaveContainer(DataContainerCreator creator) throws Exception {
		this(creator, null);
	}

	public SaveContainer(DataContainerCreator creator, EffectedFilterCalculator calculator) throws Exception {
		this.container = creator.create(this.includedData);
		this.setCalculator(calculator);
	}

	public IDataContainer getDataContainer() {
		return this.container;
	}

	public Set<IFilter> calculateEffectedFilter(final Archive archive, final IBuffer buffer) throws Exception {
		return this.calculator.calculate(archive, buffer, this.includedData);
	}

	public void postSave(final Archive archive) throws ExceptionSuppressions {
		List<Exception> errors = new ArrayList<>();
		for (Object object : includedData.keySet())
			if (object != null)
				try {
					archive.callMethod(object.getClass(), PostSave.class, object);
				} catch (Exception e) {
					errors.add(e);
				}
		if (!errors.isEmpty())
			throw new ExceptionSuppressions("Surpressed Exceptions for @PostSave Event", true).addSuppressed(errors);
	}

	public void setDataContainer(IDataContainer container) {
		this.container = container;
	}

	public void setCalculator(EffectedFilterCalculator calculator) {
		if (calculator == null)
			return;
		this.calculator = calculator;
	}

	@FunctionalInterface
	public static interface DataContainerCreator {
		IDataContainer create(final Map<Object, IQueryBuilder<?, ?, ? extends IFilter>> includedData) throws Exception;
	}

	@FunctionalInterface
	public static interface EffectedFilterCalculator {
		Set<IFilter> calculate(final Archive archive, final IBuffer buffer,
				final Map<Object, IQueryBuilder<?, ?, ? extends IFilter>> includedData) throws Exception;
	}
}
