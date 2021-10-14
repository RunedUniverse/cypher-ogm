package net.runeduniverse.libs.rogm.test.system;

import static net.runeduniverse.libs.rogm.test.system.TestEntity.*;
import static net.runeduniverse.libs.rogm.test.system.TestMsgTemplates.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import net.runeduniverse.libs.rogm.test.model.relations.ARelationEntity;

public interface TestModelRelation extends TestEntity {

	static <E extends Exception> void assertId(ARelationEntity entity) throws Exception {
		assertNotNull(entity.getMyid(), String.format(TestModelRelation_ERROR_NullId, getSimpleClassName(entity)));
	}

}
