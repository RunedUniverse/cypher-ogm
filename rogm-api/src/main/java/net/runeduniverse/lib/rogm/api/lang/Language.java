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
package net.runeduniverse.lib.rogm.api.lang;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.runeduniverse.lib.rogm.api.buffer.IBuffer;
import net.runeduniverse.lib.rogm.api.buffer.LoadState;
import net.runeduniverse.lib.rogm.api.container.IUpdatedEntryContainer;
import net.runeduniverse.lib.rogm.api.modules.IdTypeResolver;
import net.runeduniverse.lib.rogm.api.modules.Module;
import net.runeduniverse.lib.rogm.api.parser.Parser;
import net.runeduniverse.lib.rogm.api.pattern.IDataRecord;
import net.runeduniverse.lib.rogm.api.querying.IDataContainer;
import net.runeduniverse.lib.rogm.api.querying.IFRelation;
import net.runeduniverse.lib.rogm.api.querying.IFilter;

public interface Language extends DatabaseCleaner {

	Instance build(final Logger logger, final IdTypeResolver resolver, final Parser.Instance parser);

	public interface Instance {
		ILoadMapper load(IFilter filter) throws Exception;

		ISaveMapper save(IDataContainer container, Set<IFilter> filter) throws Exception;

		IDeleteMapper delete(IFilter filter, IFRelation relation) throws Exception;
	}

	public interface IMapper {
		String qry();
	}

	public interface ILoadMapper extends IMapper {
		IDataRecord parseDataRecord(List<Map<String, Module.Data>> records);
	}

	public interface ISaveMapper extends IMapper {
		<ID extends Serializable> Collection<IUpdatedEntryContainer> updateObjectIds(Map<String, ID> ids,
				LoadState loadState);
	}

	public interface IDeleteMapper extends IMapper {
		String effectedQry();

		void updateBuffer(IBuffer buffer, Serializable deletedId, List<Map<String, Object>> effectedIds);
	}
}
