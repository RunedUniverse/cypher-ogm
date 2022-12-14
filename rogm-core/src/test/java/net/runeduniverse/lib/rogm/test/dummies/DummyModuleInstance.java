/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.lib.rogm.test.dummies;

import java.io.Serializable;

import net.runeduniverse.lib.rogm.api.info.ConnectionInfo;
import net.runeduniverse.lib.rogm.api.modules.IRawDataRecord;
import net.runeduniverse.lib.rogm.api.modules.IRawIdRecord;
import net.runeduniverse.lib.rogm.api.modules.IRawRecord;
import net.runeduniverse.lib.rogm.api.modules.Module.Instance;

public class DummyModuleInstance implements Instance<Serializable> {

	@Override
	public boolean connect(ConnectionInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IRawRecord query(String qry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRawDataRecord queryObject(String qry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRawIdRecord execute(String qry) {
		// TODO Auto-generated method stub
		return null;
	}

}
