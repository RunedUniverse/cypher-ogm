/*
 * Copyright © 2023 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.rogm.modules;

import net.runeduniverse.lib.rogm.errors.ScannerException;
import net.runeduniverse.lib.rogm.pattern.Archive;
import net.runeduniverse.lib.rogm.pattern.scanner.TypeScanner;

public abstract class AModule implements Module {

	@Override
	public void configure(Archive archive) throws ScannerException {
		archive.scan(new TypeScanner.NodeScanner(archive, p -> archive.addEntry(p.getType(), p)),
				new TypeScanner.RelationScanner(archive, p -> archive.addEntry(p.getType(), p)));
	}
}
