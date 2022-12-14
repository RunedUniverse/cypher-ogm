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

import java.util.logging.Logger;

import net.runeduniverse.lib.rogm.api.lang.Language;
import net.runeduniverse.lib.rogm.api.modules.IdTypeResolver;
import net.runeduniverse.lib.rogm.api.parser.Parser;
import net.runeduniverse.lib.utils.chain.ChainManager;

public class DummyLanguage implements Language {

	@Override
	public Instance build(Logger logger, IdTypeResolver resolver, Parser.Instance parser) {
		return new DummyLanguageInstance();
	}

	@Override
	public String getChainLabel() {
		return null;
	}

	@Override
	public void setupChainManager(ChainManager chainManager) throws Exception {
		// no cleanup to be added to Chain Manger
	}

}
