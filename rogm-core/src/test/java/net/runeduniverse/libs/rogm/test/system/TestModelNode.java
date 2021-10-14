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
