package net.runeduniverse.libs.rogm.lang.cypher.pipeline.chains;

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
