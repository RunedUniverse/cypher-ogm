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
package net.runeduniverse.lib.rogm.modules.neo4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

import lombok.Getter;
import net.runeduniverse.lib.rogm.api.parser.Parser;

@Getter
public class Data implements Data {

	private Long id;
	private String entityId;
	private Set<String> labels = new HashSet<>();
	private String data;
	private String alias;

	protected Data(Parser.Instance parser, Record record, String key) throws Exception {
		this.alias = key;
		Value idProperty = record.get("id_" + key);
		if (idProperty.isNull())
			return;
		this.id = record.get("id_" + key)
				.asLong();

		Value eidProperty = record.get("eid_" + key);
		if (eidProperty.isNull())
			this.entityId = null;
		else
			this.entityId = eidProperty.asString();

		if (record.get(key)
				.isNull())
			this.data = parser.serialize(null);
		else
			this.data = parser.serialize(record.get(key)
					.asMap());

		Value labelsProperty = record.get("labels_" + key);
		if (List.class.isAssignableFrom(labelsProperty.asObject()
				.getClass()))
			for (Object o : record.get("labels_" + key)
					.asList())
				this.labels.add((String) o);
		else
			this.labels.add(labelsProperty.asString());
	}
}