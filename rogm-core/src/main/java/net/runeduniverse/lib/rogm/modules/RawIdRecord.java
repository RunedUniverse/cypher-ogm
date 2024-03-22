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
package net.runeduniverse.lib.rogm.modules;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.runeduniverse.lib.rogm.modules.Module.IRawIdRecord;

public class RawIdRecord implements IRawIdRecord {
	private final Map<String, Serializable> data;

	public RawIdRecord() {
		this.data = new HashMap<>();
	}

	public RawIdRecord(Map<String, Serializable> data) {
		this.data = data;
	}

	@Override
	public Map<String, Serializable> getIds() {
		return this.data;
	}

	public Serializable put(String alias, Serializable id) {
		return this.data.put(alias, id);
	}
}