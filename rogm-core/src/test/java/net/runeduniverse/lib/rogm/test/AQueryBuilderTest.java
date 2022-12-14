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
package net.runeduniverse.lib.rogm.test;

import java.util.Set;
import java.util.logging.Logger;

import net.runeduniverse.lib.rogm.api.Configuration;
import net.runeduniverse.lib.rogm.pattern.Archive;
import net.runeduniverse.lib.rogm.querying.QueryBuilder;
import net.runeduniverse.lib.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.lib.rogm.querying.QueryBuilder.RelationQueryBuilder;
import net.runeduniverse.lib.rogm.test.dummies.DummyLanguage;
import net.runeduniverse.lib.rogm.test.dummies.DummyModule;
import net.runeduniverse.lib.rogm.test.dummies.DummyParser;

public abstract class AQueryBuilderTest extends AArchiveTest {

	static {
		QueryBuilder.CREATOR_NODE_BUILDER = a -> new DebugNodeQueryBuilder(a);
		QueryBuilder.CREATOR_REALATION_BUILDER = a -> new DebugRelationQueryBuilder(a);
	}

	public AQueryBuilderTest() {
		super(new Configuration(new DummyParser(), new DummyLanguage(), new DummyModule(), "localhost"),
				new ConsoleLogger(Logger.getLogger(AQueryBuilderTest.class.getName())));
	}

	public AQueryBuilderTest(Configuration cnf, Logger logger) {
		super(cnf, logger);
	}

	protected static class DebugNodeQueryBuilder extends NodeQueryBuilder {
		public DebugNodeQueryBuilder(Archive archive) {
			super(archive);
		}

		public Set<RelationQueryBuilder> getRelationBuilders() {
			return super.relationBuilders;
		}
	}

	protected static class DebugRelationQueryBuilder extends RelationQueryBuilder {
		public DebugRelationQueryBuilder(Archive archive) {
			super(archive);
		}

		public NodeQueryBuilder getStart() {
			return super.startNodeBuilder;
		}

		public NodeQueryBuilder getTarget() {
			return super.targetNodeBuilder;
		}
	}
}
