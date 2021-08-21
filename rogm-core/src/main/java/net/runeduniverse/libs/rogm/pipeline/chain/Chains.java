package net.runeduniverse.libs.rogm.pipeline.chain;

public interface Chains {

	public interface LOAD_CHAIN {
		public interface ALL {
			public static final String LABEL = "LOAD_CHAIN.ALL";

			public static final int BUILD_QUERY_MAPPER = 100;
			public static final int QUERY_DATABASE_FOR_RAW_DATA_RECORD = 200;
			public static final int CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD = 300;
			public static final int ASSEMBLY_ENTITY_COLLECTION = 400;
		}

		public interface ONE {
			public static final String LABEL = "LOAD_CHAIN.ONE";

			public static final int CHECK_BUFFERED_STATUS = 0;
			public static final int BUILD_QUERY_MAPPER = 100;
			public static final int QUERY_DATABASE_FOR_RAW_DATA_RECORD = 200;
			public static final int CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD = 300;
			public static final int ASSEMBLY_ENTITY_COLLECTION = 400;
			public static final int REDUCE_COLLECTION = 450;
		}
	}

	public interface RELOAD_CHAIN {
		public interface ALL {
			public static final String LABEL = "RELOAD_CHAIN.ALL";

			public static final int UPDATE_ENTITY_COLLECTION = 400;
		}
	}

	public interface BUFFER_CHAIN {

		public interface LOAD {
			public static final String LABEL = "BUFFER_CHAIN.LOAD";

			public static final int PREPARE_DATA = 10;
			public static final int ACQUIRE_BUFFERED_ENTITY = 20;
			public static final int DESERIALIZE_DATA = 30;
			public static final int ACQUIRE_NEW_ENTITY = 40;
		}

		public interface UPDATE {
			public static final String LABEL = "BUFFER_CHAIN.UPDATE";

			public static final int PREPARE_DATA = 10;
			public static final int VALIDATE_ENTITY = 20;
			public static final int DESERIALIZE_DATA = 30;
			public static final int UPDATE_BUFFERED_ENTRY = 40;
		}
	}

}
