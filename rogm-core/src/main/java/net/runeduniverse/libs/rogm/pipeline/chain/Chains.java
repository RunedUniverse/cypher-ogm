package net.runeduniverse.libs.rogm.pipeline.chain;

public interface Chains {

	public interface LOAD_CHAIN {
		public interface ALL {
			public static final String LABEL = "LOAD_CHAIN.ALL";

			public static final int BUILD_QUERY_MAPPER = 100;
			public static final int QUERY_DATABASE_FOR_RAW_DATA_RECORD = 200;
			public static final int CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD = 300;
			public static final int ASSEMBLY_ENTITY_COLLECTION = 400;
			public static final int RESOLVE_DEPTH = 500;
		}

		public interface ONE {
			public static final String LABEL = "LOAD_CHAIN.ONE";

			public static final int CHECK_BUFFERED_STATUS = 0;
			public static final int BUILD_QUERY_MAPPER = 100;
			public static final int QUERY_DATABASE_FOR_RAW_DATA_RECORD = 200;
			public static final int CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD = 300;
			public static final int ASSEMBLY_ENTITY_COLLECTION = 400;
			public static final int REDUCE_COLLECTION = 450;
			public static final int RESOLVE_DEPTH = 500;
		}

		public interface RESOLVE_LAZY {
			public interface ALL {
				public static final String LABEL = "LOAD_CHAIN.RESOLVE_LAZY.ALL";

				public static final int VALIDATE_LAZY_ENTRIES = 100;
				public static final int CALL_RESOLVE_SELECTED = 200;
				public static final int LOOP_LAZY_ENTRIES = 300;
			}

			public interface SELECTED {
				public static final String LABEL = "LOAD_CHAIN.RESOLVE_LAZY.SELECTED";

				public static final int BUILD_QUERY_MAPPER = 100;
				public static final int QUERY_DATABASE_FOR_RAW_DATA_RECORD = 200;
				public static final int CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD = 300;
				public static final int ASSEMBLY_ENTITY_COLLECTION = 400;
			}
		}
	}

	public interface RELOAD_CHAIN {
		public interface ALL {
			public static final String LABEL = "RELOAD_CHAIN.ALL";

			public static final int CALL_RELOAD_SELECTED = 100;
			public static final int VALIDATE_RELATED_ENTRIES = 200;
			public static final int CALL_RELOAD_SELECTED_FOR_RELATED = 300;
			public static final int LOOP_RELATED_ENTRIES = 400;
		}

		public interface SELECTED {
			public static final String LABEL = "RELOAD_CHAIN.SELECTED";

			public static final int BUILD_QUERY_MAPPER = 100;
			public static final int QUERY_DATABASE_FOR_RAW_DATA_RECORD = 200;
			public static final int CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD = 300;
			public static final int UPDATE_ENTITY_COLLECTION = 400;
		}
	}

	public interface SAVE_CHAIN {
		public interface ONE {
			public static final String LABEL = "SAVE_CHAIN.ONE";

			public static final int BUILD_QUERY_MAPPER = 100;
			public static final int QUERY_DATABASE_FOR_RAW_ID_RECORD = 200;
			public static final int COLLECT_UPDATED_ENTRIES = 300;
			public static final int UPDATE_BUFFER_ENTRIES = 400;
			public static final int CALL_DATABASE_CLEANUP = 500;
			public static final int POST_SAVE_EVENT = 550;
		}
	}

	public interface DELETE_CHAIN {
		public interface ONE {
			public static final String LABEL = "DELETE_CHAIN.ONE";

			public static final int GET_BUFFERED_ENTRY = 100;
			public static final int PACKAGE_CONTAINER = 200;
			public static final int BUILD_QUERY_MAPPER = 300;
			public static final int QUERY_DATABASE_FOR_RAW_RECORD = 400;
			public static final int UPDATE_BUFFER = 500;
			public static final int EXECUTE_DELETION_ON_DATABASE = 600;
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
