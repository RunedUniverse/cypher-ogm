package net.runeduniverse.libs.rogm.pipeline;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Chain {
	String label();

	int layer();

	public static interface Load {
		public static final String LABEL = "LOAD";
		public static final int BUFFER_REQUEST = 1000;
		public static final int BUILD_FILTER = 2000;

	}

	public static interface Reload {
		public static final String LABEL = "RELOAD";

	}

	public static interface Save {
		public static final String LABEL = "SAVE";

	}

	public static interface Delete {
		public static final String LABEL = "DELETE";

	}
}
