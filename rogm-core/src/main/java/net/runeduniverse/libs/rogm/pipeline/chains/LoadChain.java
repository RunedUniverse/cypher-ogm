package net.runeduniverse.libs.rogm.pipeline.chains;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Comparator;

@Retention(RUNTIME)
@Target(METHOD)
public @interface LoadChain {

	public static final int INIT = 0;
	/*
	 * PRE_FILTER =
	 */

	public static final Comparator<LoadChain> COMPARATOR = new Comparator<LoadChain>() {

		@Override
		public int compare(LoadChain o1, LoadChain o2) {
			if (o1.level() < o2.level())
				return -1;

			if (o1.level() > o2.level())
				return 11;
			return 0;
		}
	};

	public int level();
}
