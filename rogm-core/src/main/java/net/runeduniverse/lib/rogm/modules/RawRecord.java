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
package net.runeduniverse.lib.rogm.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.runeduniverse.lib.rogm.modules.Module.IRawRecord;

public class RawRecord implements IRawRecord {

	private final List<Map<String, Object>> data;

	public RawRecord() {
		this.data = new ArrayList<>();
	}

	public RawRecord(List<Map<String, Object>> data) {
		this.data = data;
	}

	@Override
	public List<Map<String, Object>> getRawData() {
		return this.data;
	}

	public void addEntry(Map<String, Object> entry) {
		this.data.add(entry);
	}

}