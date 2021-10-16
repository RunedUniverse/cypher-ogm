package net.runeduniverse.libs.rogm.test;

import java.util.Set;
import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.RelationQueryBuilder;
import net.runeduniverse.libs.rogm.test.dummies.DummyLanguage;
import net.runeduniverse.libs.rogm.test.dummies.DummyModule;
import net.runeduniverse.libs.rogm.test.dummies.DummyParser;

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
