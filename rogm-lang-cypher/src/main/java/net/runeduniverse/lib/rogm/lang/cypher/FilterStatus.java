/*
 * Copyright © 2022 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.rogm.lang.cypher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FilterStatus {
	public static final FilterStatus INITIALIZED = new FilterStatus(1);
	public static final FilterStatus PRE_PRINTED = new FilterStatus(2);
	public static final FilterStatus PRINTED = new FilterStatus(3);
	public static final FilterStatus EXTENSION_PRINTED = new FilterStatus(4);

	@Getter
	private int status = 0;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FilterStatus)
			return this.status >= ((FilterStatus) obj).getStatus();
		return super.equals(obj);
	}
}