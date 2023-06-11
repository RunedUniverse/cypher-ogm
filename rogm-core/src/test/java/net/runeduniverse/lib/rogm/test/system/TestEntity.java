/*
 * Copyright © 2022 VenaNocta (venanocta@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.rogm.test.system;

import static net.runeduniverse.lib.rogm.test.system.TestMsgTemplates.*;

import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;

public interface TestEntity {

	static void infoTesting(Logger logger, Object entity) {
		logger.info(String.format(TestModelEntity_INFO_Testing, getSimpleClassName(entity), entity));
	}

	static <E extends Exception> void assertEntity(Class<?> expectedClass, Object entity) throws E {
		Assertions.assertTrue(expectedClass.isInstance(entity),
				String.format(TestModelEntity_ERROR_ExpectedClassObject, expectedClass.getSimpleName(), entity));
	}

	static String getSimpleClassName(Object entity) {
		return entity == null ? null
				: entity.getClass()
						.getSimpleName();
	}
}
