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
package net.runeduniverse.libs.rogm.modules;

import net.runeduniverse.libs.rogm.errors.ScannerException;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.scanner.TypeScanner;

public abstract class AModule implements Module {

	@Override
	public void configure(Archive archive) throws ScannerException {
		archive.scan(new TypeScanner.NodeScanner(archive, p -> archive.addEntry(p.getType(), p)),
				new TypeScanner.RelationScanner(archive, p -> archive.addEntry(p.getType(), p)));
	}
}
