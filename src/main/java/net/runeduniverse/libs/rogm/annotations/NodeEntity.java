package net.runeduniverse.libs.rogm.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface NodeEntity{
	String label() default "";
	InterpretationMode mode() default InterpretationMode.IMPLICIT;
}
