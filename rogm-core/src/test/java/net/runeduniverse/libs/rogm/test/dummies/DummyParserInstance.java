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
package net.runeduniverse.libs.rogm.test.dummies;

import net.runeduniverse.libs.rogm.parser.Parser.Instance;

public class DummyParserInstance implements Instance{

	@Override
	public String serialize(Object object) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T deserialize(Class<T> clazz, String value) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> void deserialize(T obj, String value) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
