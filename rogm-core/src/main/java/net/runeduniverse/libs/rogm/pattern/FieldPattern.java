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
package net.runeduniverse.libs.rogm.pattern;

import java.lang.reflect.Field;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Converter;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;

@ToString(callSuper = true)
public class FieldPattern extends net.runeduniverse.libs.scanner.FieldPattern {

	protected final Archive archive;
	@Getter
	@Setter
	protected IConverter<?> converter = null;

	public FieldPattern(Archive archive, Field field) throws Exception {
		super(field);
		this.archive = archive;
		Converter converterAnno = this.field.getAnnotation(Converter.class);
		if (converterAnno == null) {
			if (this.field.isAnnotationPresent(Id.class))
				this.converter = IConverter.createConverter(null, field.getType());
		} else
			this.converter = IConverter.createConverter(converterAnno, field.getType());
	}
}
