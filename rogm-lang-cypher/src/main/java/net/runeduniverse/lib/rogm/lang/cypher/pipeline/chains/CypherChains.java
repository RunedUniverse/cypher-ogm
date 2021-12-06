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
package net.runeduniverse.lib.rogm.lang.cypher.pipeline.chains;

public interface CypherChains {

	public interface DATABASE_CLEANUP_CHAIN {
		public interface DROP_REMOVED_RELATIONS {
			public static final String LABEL = "DATABASE_CLEANUP_CHAIN.DROP_REMOVED_RELATIONS";

			public static final int REDUCE_LAYERS = 10;
			public static final int BUILD_QUERY = 20;
			public static final int EXECUTE_ON_DATABASE = 30;
		}
	}
}
