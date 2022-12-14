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

import net.runeduniverse.lib.rogm.api.modules.IRawDataRecord;
import net.runeduniverse.lib.rogm.api.modules.Module;

public class RawDataRecord implements IRawDataRecord {

	private final List<Map<String, Module.Data>> data;

	public RawDataRecord() {
		this.data = new ArrayList<>();
	}

	public RawDataRecord(List<Map<String, Module.Data>> data) {
		this.data = data;
	}

	@Override
	public List<Map<String, Module.Data>> getData() {
		return this.data;
	}

	public void addEntry(Map<String, Module.Data> entry) {
		this.data.add(entry);
	}

}