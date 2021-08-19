package net.runeduniverse.libs.rogm.pipeline.chain;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chains;

@Retention(RUNTIME)
@Target(METHOD)
@Repeatable(Chains.class)
public @interface Chain {
	public static final String LOAD_ALL_CHAIN = "LOAD_ALL";
	public static final String LOAD_ONE_CHAIN = "LOAD_ONE";

	public static final String BUFFER_LOAD_CHAIN = "BUFFER_LOAD";

	String label();

	int[] layers();

	boolean ignoreCancelled() default false;
}
