package net.runeduniverse.libs.rogm.pattern.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.PostDelete;
import net.runeduniverse.libs.rogm.annotations.PostLoad;
import net.runeduniverse.libs.rogm.annotations.PostReload;
import net.runeduniverse.libs.rogm.annotations.PostSave;
import net.runeduniverse.libs.rogm.annotations.PreDelete;
import net.runeduniverse.libs.rogm.annotations.PreReload;
import net.runeduniverse.libs.rogm.annotations.PreSave;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.annotations.StartNode;
import net.runeduniverse.libs.rogm.annotations.TargetNode;
import net.runeduniverse.libs.rogm.pattern.APattern;
import net.runeduniverse.libs.rogm.pattern.FieldPattern;
import net.runeduniverse.libs.rogm.pattern.IStorage;
import net.runeduniverse.libs.rogm.pattern.NodePattern;
import net.runeduniverse.libs.rogm.pattern.RelationPattern;
import net.runeduniverse.libs.rogm.scanner.MethodAnnotationScanner;
import net.runeduniverse.libs.rogm.scanner.MethodPattern;
import net.runeduniverse.libs.rogm.scanner.ResultConsumer;
import net.runeduniverse.libs.rogm.scanner.ScanOrder;
import net.runeduniverse.libs.rogm.scanner.TypeAnnotationScanner;

public class TypeScanner extends TypeAnnotationScanner<FieldPattern, MethodPattern, APattern> {

	protected final IStorage factory;

	public TypeScanner(IStorage factory, PatternCreator<FieldPattern, MethodPattern, APattern> creator,
			Class<? extends Annotation> anno, ResultConsumer consumer) {
		super(anno, creator, consumer);
		this.factory = factory;
		// Fields
		this.addFieldScanner(new IdFieldScanner());
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
	public void scan(Class<?> type, ClassLoader loader, String pkg) {
		if (Modifier.isAbstract(type.getModifiers()))
			return;
		super.scan(type, loader, pkg);
	}

	public static class NodeScanner extends TypeScanner {

		public NodeScanner(IStorage factory, ResultConsumer consumer) {
			super(factory, (type, loader, pkg) -> new NodePattern(factory, pkg, loader, type), NodeEntity.class,
					consumer);
		}
	}

	public static class RelationScanner extends TypeScanner {

		public RelationScanner(IStorage factory, ResultConsumer consumer) {
			super(factory, (type, loader, pkg) -> new RelationPattern(factory, pkg, loader, type),
					RelationshipEntity.class, consumer);
			// Fields
			this.addFieldScanner(new FieldAnnotationScanner(StartNode.class, ScanOrder.FIRST));
			this.addFieldScanner(new FieldAnnotationScanner(TargetNode.class, ScanOrder.FIRST));
		}
	}
}
