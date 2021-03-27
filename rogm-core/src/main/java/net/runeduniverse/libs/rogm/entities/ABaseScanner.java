package net.runeduniverse.libs.rogm.entities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import net.runeduniverse.libs.rogm.annotations.PostDelete;
import net.runeduniverse.libs.rogm.annotations.PostLoad;
import net.runeduniverse.libs.rogm.annotations.PostReload;
import net.runeduniverse.libs.rogm.annotations.PostSave;
import net.runeduniverse.libs.rogm.annotations.PreDelete;
import net.runeduniverse.libs.rogm.annotations.PreReload;
import net.runeduniverse.libs.rogm.annotations.PreSave;
import net.runeduniverse.libs.rogm.entities.base.IdFieldScanner;
import net.runeduniverse.libs.rogm.scanner.MethodAnnotationScanner;
import net.runeduniverse.libs.rogm.scanner.ResultConsumer;
import net.runeduniverse.libs.rogm.scanner.ScanOrder;
import net.runeduniverse.libs.rogm.scanner.TypeAnnotationScanner;
import net.runeduniverse.libs.rogm.scanner.TypePattern;

public class ABaseScanner extends TypeAnnotationScanner {

	public ABaseScanner(Class<? extends Annotation> anno, ResultConsumer consumer) {
		super(anno, consumer);
		// Fields
		this.addFieldScanner(new IdFieldScanner());
		// Events
		this.addFieldScanner(new MethodAnnotationScanner(PreReload.class, ScanOrder.FIRST));
		this.addFieldScanner(new MethodAnnotationScanner(PreSave.class, ScanOrder.FIRST));
		this.addFieldScanner(new MethodAnnotationScanner(PreDelete.class, ScanOrder.FIRST));
		this.addFieldScanner(new MethodAnnotationScanner(PostLoad.class, ScanOrder.FIRST));
		this.addFieldScanner(new MethodAnnotationScanner(PostReload.class, ScanOrder.FIRST));
		this.addFieldScanner(new MethodAnnotationScanner(PostSave.class, ScanOrder.FIRST));
		this.addFieldScanner(new MethodAnnotationScanner(PostDelete.class, ScanOrder.FIRST));
	}
	
	@Override
	protected TypePattern createPattern(Class<?> type, ClassLoader loader, String pkg) {
		return new Pattern(pkg, loader, type);
	}

	@Override
	public void scan(Class<?> type, ClassLoader loader, String pkg) {
		if (Modifier.isAbstract(type.getModifiers()))
			return;
		super.scan(type, loader, pkg);
	}

}
