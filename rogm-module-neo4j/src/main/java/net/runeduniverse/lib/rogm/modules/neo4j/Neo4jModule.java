/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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

import java.util.logging.Logger;

import net.runeduniverse.lib.rogm.info.ConnectionInfo;
import net.runeduniverse.lib.rogm.modules.AModule;
import net.runeduniverse.lib.rogm.parser.Parser;

public class Neo4jModule extends AModule {

	private static final String ID_ALIAS = "_id";

	@Override
	public Instance<Long> build(final Logger logger, final Parser.Instance parser) {
		return new Neo4jModuleInstance(parser, logger);
	}

	public static String buildUri(ConnectionInfo info) {
		return info.getProtocol() + "://" + info.getUri() + ':' + info.getPort();
	}

	@Override
	public Class<?> idType() {
		return Long.class;
	}

	@Override
	public boolean checkIdType(Class<?> type) {
		if (type == null)
			return false;
		return Number.class.isAssignableFrom(type);
	}

	public String getIdAlias() {
		return ID_ALIAS;
	}
}
