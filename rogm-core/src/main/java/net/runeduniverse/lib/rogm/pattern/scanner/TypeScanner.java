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
import java.lang.reflect.Modifier;

import net.runeduniverse.lib.rogm.api.annotations.Converter;
import net.runeduniverse.lib.rogm.api.annotations.Id;
import net.runeduniverse.lib.rogm.api.annotations.NodeEntity;
import net.runeduniverse.lib.rogm.api.annotations.PostDelete;
import net.runeduniverse.lib.rogm.api.annotations.PostLoad;
import net.runeduniverse.lib.rogm.api.annotations.PostReload;
import net.runeduniverse.lib.rogm.api.annotations.PostSave;
import net.runeduniverse.lib.rogm.api.annotations.PreDelete;
import net.runeduniverse.lib.rogm.api.annotations.PreReload;
import net.runeduniverse.lib.rogm.api.annotations.PreSave;
import net.runeduniverse.lib.rogm.api.annotations.Relationship;
import net.runeduniverse.lib.rogm.api.annotations.RelationshipEntity;
import net.runeduniverse.lib.rogm.api.annotations.StartNode;
import net.runeduniverse.lib.rogm.api.annotations.TargetNode;
import net.runeduniverse.lib.rogm.api.pattern.IArchive;
import net.runeduniverse.lib.rogm.pattern.APattern;
import net.runeduniverse.lib.rogm.pattern.FieldPattern;
import net.runeduniverse.lib.rogm.pattern.NodePattern;
import net.runeduniverse.lib.rogm.pattern.RelationPattern;
import net.runeduniverse.lib.utils.scanner.pattern.MethodPattern;
import net.runeduniverse.lib.utils.scanner.templates.MethodAnnotationScanner;
import net.runeduniverse.lib.utils.scanner.templates.TypeAnnotationScanner;
import net.runeduniverse.lib.utils.scanner.ScanOrder;

public class TypeScanner extends TypeAnnotationScanner<FieldPattern, MethodPattern, APattern<?>> {

	protected final IArchive archive;

	public TypeScanner(IArchive archive, PatternCreator<FieldPattern, MethodPattern, APattern<?>> creator,
			Class<? extends Annotation> anno, ResultConsumer<FieldPattern, MethodPattern, APattern<?>> consumer) {
		super(anno, creator, consumer);
		this.archive = archive;

		// Fields
		this.addFieldScanner(new FieldAnnoScanner(archive, Id.class, ScanOrder.FIRST));
		this.addFieldScanner(new FieldAnnoScanner(archive, Converter.class, ScanOrder.ALL));
		// Events
		this.addFieldScanner(MethodAnnotationScanner.DEFAULT(PreReload.class, ScanOrder.FIRST));
		this.addFieldScanner(MethodAnnotationScanner.DEFAULT(PreSave.class, ScanOrder.FIRST));
		this.addFieldScanner(MethodAnnotationScanner.DEFAULT(PreDelete.class, ScanOrder.FIRST));
		this.addFieldScanner(MethodAnnotationScanner.DEFAULT(PostLoad.class, ScanOrder.FIRST));
		this.addFieldScanner(MethodAnnotationScanner.DEFAULT(PostReload.class, ScanOrder.FIRST));
		this.addFieldScanner(MethodAnnotationScanner.DEFAULT(PostSave.class, ScanOrder.FIRST));
		this.addFieldScanner(MethodAnnotationScanner.DEFAULT(PostDelete.class, ScanOrder.FIRST));
	}

	@Override
	public void scan(Class<?> type, ClassLoader loader, String pkg) throws Exception {
		if (Modifier.isAbstract(type.getModifiers()))
			return;
		super.scan(type, loader, pkg);
	}

	public static class NodeScanner extends TypeScanner {

		public NodeScanner(IArchive archive, ResultConsumer<FieldPattern, MethodPattern, APattern<?>> consumer) {
			super(archive, (type, loader, pkg) -> new NodePattern(archive, pkg, loader, type), NodeEntity.class,
					consumer);
			// Fields
			this.addFieldScanner(new RelatedFieldAnnoScanner(archive, Relationship.class, ScanOrder.FIRST));
		}
	}

	public static class RelationScanner extends TypeScanner {

		public RelationScanner(IArchive archive, ResultConsumer<FieldPattern, MethodPattern, APattern<?>> consumer) {
			super(archive, (type, loader, pkg) -> new RelationPattern(archive, pkg, loader, type),
					RelationshipEntity.class, consumer);
			// Fields
			this.addFieldScanner(new FieldAnnoScanner(archive, StartNode.class, ScanOrder.FIRST));
			this.addFieldScanner(new FieldAnnoScanner(archive, TargetNode.class, ScanOrder.FIRST));
		}
	}
}
