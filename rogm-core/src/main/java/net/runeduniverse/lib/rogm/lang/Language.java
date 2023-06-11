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
package net.runeduniverse.lib.rogm.lang;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.runeduniverse.lib.rogm.buffer.IBuffer;
import net.runeduniverse.lib.rogm.buffer.BufferTypes.LoadState;
import net.runeduniverse.lib.rogm.modules.IdTypeResolver;
import net.runeduniverse.lib.rogm.modules.Module.Data;
import net.runeduniverse.lib.rogm.parser.Parser;
import net.runeduniverse.lib.rogm.pattern.IPattern;
import net.runeduniverse.lib.rogm.pipeline.chain.data.UpdatedEntryContainer;
import net.runeduniverse.lib.rogm.querying.IDataContainer;
import net.runeduniverse.lib.rogm.querying.IFRelation;
import net.runeduniverse.lib.rogm.querying.IFilter;

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
		IPattern.IDataRecord parseDataRecord(List<Map<String, Data>> records);
	}

	public interface ISaveMapper extends IMapper {
		<ID extends Serializable> Collection<UpdatedEntryContainer> updateObjectIds(Map<String, ID> ids,
				LoadState loadState);
	}

	public interface IDeleteMapper extends IMapper {
		String effectedQry();

		void updateBuffer(IBuffer buffer, Serializable deletedId, List<Map<String, Object>> effectedIds);
	}
}
