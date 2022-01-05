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
package net.runeduniverse.lib.rogm.querying;

import lombok.Getter;

public abstract class AFilter<F extends IFilter> implements IReturned, IOptional {

	protected F instance = null;
	@Getter
	private boolean returned = false;
	@Getter
	private boolean optional = false;
	@Getter
	private FilterType filterType = FilterType.MATCH;

	protected void setInstance(F instance) {
		this.instance = instance;
	}

	public F setReturned(boolean returning) {
		this.returned = returning;
		return this.instance;
	}

	public F setOptional(boolean optional) {
		this.optional = optional;
		return this.instance;
	}
}
