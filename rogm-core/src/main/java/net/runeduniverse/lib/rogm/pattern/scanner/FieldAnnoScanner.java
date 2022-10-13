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
package net.runeduniverse.lib.rogm.pattern.scanner;

import java.lang.annotation.Annotation;

import net.runeduniverse.lib.rogm.pattern.Archive;
import net.runeduniverse.lib.rogm.pattern.FieldPattern;
import net.runeduniverse.lib.utils.scanner.ScanOrder;
import net.runeduniverse.lib.utils.scanner.templates.FieldScanner;

public class FieldAnnoScanner extends net.runeduniverse.lib.utils.scanner.templates.FieldAnnotationScanner<FieldPattern> {

	public FieldAnnoScanner(Archive archive, Class<? extends Annotation> anno) {
		super(creator(archive), anno);
	}

	public FieldAnnoScanner(Archive archive, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator(archive), anno, order);
	}

	public FieldAnnoScanner(FieldScanner.PatternCreator<FieldPattern> creator, Class<? extends Annotation> anno) {
		super(creator, anno);
	}

	public FieldAnnoScanner(FieldScanner.PatternCreator<FieldPattern> creator, Class<? extends Annotation> anno,
			ScanOrder order) {
		super(creator, anno, order);
	}

	private static FieldScanner.PatternCreator<FieldPattern> creator(Archive archive) {
		return f -> new FieldPattern(archive, f);
	}
}
