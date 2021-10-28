/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.libs.rogm.test.system;

import static net.runeduniverse.libs.rogm.test.system.TestEntity.*;
import static net.runeduniverse.libs.rogm.test.system.TestMsgTemplates.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import net.runeduniverse.libs.rogm.test.model.AEntity;

public interface TestModelNode extends TestEntity {

	static <E extends Exception> void assertId(AEntity entity) throws Exception {
		assertNotNull(entity.getMyid(), String.format(TestModelNode_ERROR_NullId, getSimpleClassName(entity)));
	}
}
