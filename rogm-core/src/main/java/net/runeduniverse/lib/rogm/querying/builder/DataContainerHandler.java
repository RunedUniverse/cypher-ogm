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
package net.runeduniverse.lib.rogm.querying.builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.lib.rogm.querying.FilterType;
import net.runeduniverse.lib.rogm.querying.IDataContainer;
import net.runeduniverse.lib.utils.logging.logs.CompoundTree;

@NoArgsConstructor
@Setter
public class DataContainerHandler implements IDataContainer, NoFilterType, ITraceable {

	@Getter
	private Object data = null;
	private boolean persist = false;
	@Getter
	private boolean readonly = false;

	@Override
	public boolean persist() {
		return this.persist;
	}

	@Override
	public void toRecord(CompoundTree tree) {
		tree.append("DATA", data == null ? "null" : data.toString())
				.append("PERSIST", this.persist ? "TRUE" : "FALSE")
				.append("READONLY", this.readonly ? "TRUE" : "FALSE");
	}

	public static boolean required(DataContainerHandler instance, final FilterType filterType) {
		if (filterType == FilterType.CREATE || filterType == FilterType.UPDATE)
			return true;
		if (instance == null)
			return false;
		return instance.data != null || instance.persist || instance.readonly;
	}
}
