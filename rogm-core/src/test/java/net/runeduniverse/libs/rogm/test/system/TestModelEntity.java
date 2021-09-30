package net.runeduniverse.libs.rogm.test.system;

import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;

import static net.runeduniverse.libs.rogm.test.system.TestMsgTemplates.*;

public interface TestModelEntity {

	static void infoTesting(Logger logger, Object entity) {
		logger.info(String.format(TestModelEntity_INFO_Testing, getSimpleClassName(entity), entity));
	}

	static <E extends Exception> void assertEntity(Class<?> expectedClass, Object entity) throws E {
		Assertions.assertNotNull(entity,
				String.format(TestModelEntity_ERROR_ExpectedClassObject, expectedClass.getSimpleName()));
	}

	static String getSimpleClassName(Object entity) {
		return entity == null ? null
				: entity.getClass()
						.getSimpleName();
	}
}
