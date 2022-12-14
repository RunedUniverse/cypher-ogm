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

import java.util.Collection;

public interface IValidatable {
	
	void validate() throws Exception;

	public static void validate(Object... values) throws Exception {
		if (values == null)
			return;

		for (Object obj : values) {
			if (obj instanceof IValidatable)
				((IValidatable) obj).validate();
			if (obj instanceof Collection<?>)
				for (Object element : (Collection<?>) obj)
					IValidatable.validate(element);
		}
	}
}
