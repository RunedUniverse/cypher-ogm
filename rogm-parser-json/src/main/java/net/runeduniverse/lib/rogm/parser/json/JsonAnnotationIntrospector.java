/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.rogm.parser.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.lib.rogm.annotations.Id;
import net.runeduniverse.lib.rogm.annotations.Property;
import net.runeduniverse.lib.rogm.annotations.Relationship;
import net.runeduniverse.lib.rogm.annotations.StartNode;
import net.runeduniverse.lib.rogm.annotations.TargetNode;
import net.runeduniverse.lib.rogm.annotations.Transient;
import net.runeduniverse.lib.rogm.modules.IdTypeResolver;

@RequiredArgsConstructor
public class JsonAnnotationIntrospector extends NopAnnotationIntrospector {
	private static final long serialVersionUID = 1L;
	private final IdTypeResolver resolver;

	@Override
	public Value findPropertyInclusion(Annotated a) {
		return _isProperty(a);
	}

	@Override
	public boolean hasIgnoreMarker(AnnotatedMember m) {
		return _isTransient(m) || 2 == _isId(m) || _isRelationship(m) || _isStartNode(m) || _isTargetNode(m);
	}

	@Override
	public PropertyName findNameForSerialization(Annotated a) {
		if (1 == _isId(a)) {
			return PropertyName.construct(resolver.getIdAlias());
		}
		return null;
	}

	@Override
	public PropertyName findNameForDeserialization(Annotated a) {
		if (1 == _isId(a))
			return PropertyName.construct(resolver.getIdAlias());
		return null;
	}

	private JsonInclude.Value _isProperty(Annotated a) {
		Property anno = _findAnnotation(a, Property.class);
		if (anno == null)
			return JsonInclude.Value.empty();
		return JsonInclude.Value.empty()
				.withValueInclusion(Include.ALWAYS)
				.withContentInclusion(Include.ALWAYS)
				.withValueFilter(Void.class)
				.withContentFilter(Void.class);
	}

	private boolean _isTransient(Annotated a) {
		Transient anno = _findAnnotation(a, Transient.class);
		if (anno == null)
			return false;
		return anno.value();
	}

	private short _isId(Annotated a) {
		Id anno = _findAnnotation(a, Id.class);
		if (anno == null)
			return 0;
		if (resolver.checkIdType(a.getRawType()))
			return 2;
		return 1;
	}

	private boolean _isRelationship(Annotated a) {
		Relationship anno = _findAnnotation(a, Relationship.class);
		if (anno == null)
			return false;
		return true;
	}

	private boolean _isStartNode(Annotated a) {
		StartNode anno = _findAnnotation(a, StartNode.class);
		if (anno == null)
			return false;
		return true;
	}

	private boolean _isTargetNode(Annotated a) {
		TargetNode anno = _findAnnotation(a, TargetNode.class);
		if (anno == null)
			return false;
		return true;
	}

}