/*
 * Copyright © 2023 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.rogm.querying;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

public abstract class AParamFilter<F extends IFilter> extends AParamHolder<F> implements ILabeled {

	@Getter
	protected Set<String> labels = new HashSet<>();

	// LABEL
	public F addLabel(String label) {
		this.labels.add(label);
		return this.instance;
	}

	public F addLabels(Collection<String> labels) {
		this.labels.addAll(labels);
		return this.instance;
	}
}
