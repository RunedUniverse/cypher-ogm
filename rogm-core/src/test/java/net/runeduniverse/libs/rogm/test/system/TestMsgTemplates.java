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

public interface TestMsgTemplates {

	public static final String TestModelEntity_INFO_Testing = "TESTING >> %s\n%s";
	public static final String TestModelEntity_ERROR_ExpectedClassObject = "Object of Class<%s> expected got: %s";

	public static final String TestModelNode_ERROR_NullId = "AEntity.myid of Class<%s> is null";

	public static final String TestModelRelation_ERROR_NullId = "ARelationEntity.myid of Class<%s> is null";
}
