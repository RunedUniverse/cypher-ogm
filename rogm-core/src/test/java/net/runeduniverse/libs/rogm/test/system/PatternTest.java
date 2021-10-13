package net.runeduniverse.libs.rogm.test.system;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import lombok.NoArgsConstructor;
import net.runeduniverse.libs.rogm.pattern.APattern;
import net.runeduniverse.libs.rogm.pattern.FieldPattern;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.test.model.Artist;

@NoArgsConstructor
public class PatternTest extends ArchiveTest {

	@SuppressWarnings("rawtypes")
	public static Class<APattern> UNLOCKED_APATTERN_CLASS = APattern.class;
	public static Field UNLOCKED_APATTERN_ID_FIELD;
	public static final String APATTERN_ID_FIELD_NAME = "idFieldPattern";

	@BeforeAll
	@Tag("system")
	public static void prepare() throws Exception {
		for (Class<?> clazz = UNLOCKED_APATTERN_CLASS; clazz != Object.class; clazz = clazz.getSuperclass()) {
			unlockClass(clazz);
		}
		assertNotNull(UNLOCKED_APATTERN_ID_FIELD,
				"Field of name: \"" + APATTERN_ID_FIELD_NAME + "\" could not be collected from Class<APattern>");
		assertTrue(FieldPattern.class.isAssignableFrom(UNLOCKED_APATTERN_ID_FIELD.getType()),
				"collected Field of name: \"" + APATTERN_ID_FIELD_NAME + "\" is not of Class<FieldPattern>");
	}

	private static void unlockClass(Class<?> clazz) throws Exception {
		for (Field f : clazz.getDeclaredFields()) {
			f.setAccessible(true);
			if (f.getName()
					.equals(APATTERN_ID_FIELD_NAME))
				UNLOCKED_APATTERN_ID_FIELD = f;
		}
		for (Method m : clazz.getDeclaredMethods()) {
			m.setAccessible(true);
		}
	}

	@Test
	@Tag("system")
	public void existenceCheckArtist() throws Exception {
		IBaseQueryPattern<?> pattern = this.archive.getPattern(Artist.class, IBaseQueryPattern.class);
		if (pattern instanceof APattern<?>) {
			assertTrue(((APattern<?>) pattern).isValid(), "Object of Class<" + pattern.getClass()
					.getSimpleName() + "> NEVER got validated!");
			FieldPattern fieldPattern = (FieldPattern) UNLOCKED_APATTERN_ID_FIELD.get(pattern);
			assertNotNull(fieldPattern,
					"Field APattern<?>." + APATTERN_ID_FIELD_NAME + " is NULL in Object of Class<" + pattern.getClass()
							.getSimpleName() + ">");
		}
	}
}
