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
package net.runeduniverse.lib.rogm.api.modules;

import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;

import net.runeduniverse.lib.rogm.api.info.ConnectionInfo;
import net.runeduniverse.lib.rogm.api.parser.Parser;

public interface Module extends PassiveModule, IdTypeResolver {

	// params might not get used from every module
	// but are provided if needed
	Instance<?> build(final Logger logger, final Parser.Instance parser);

	public interface Instance<ID extends Serializable> {
		boolean connect(ConnectionInfo info);

		boolean disconnect();

		boolean isConnected();

		IRawRecord query(String qry);

		IRawDataRecord queryObject(String qry);

		IRawIdRecord execute(String qry);
	}
	
	public interface Data {
		Serializable getId();

		String getEntityId();

		Set<String> getLabels();

		String getData();

		String getAlias();
	}
}
