package net.runeduniverse.libs.rogm.scanner;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnnotationScanner implements IScanner {
	private final Class<? extends Annotation> anno;
	private Set<IScanner> follower = new HashSet<>();

	public AnnotationScanner addFollower(IScanner scanner) {
		this.follower.add(scanner);
		return this;
	}

	@Override
	public void scan(Class<?> clazz, ClassLoader loader, String pkg) {
		if (clazz.isAnnotationPresent(this.anno))
			for (IScanner scanner : follower)
				scanner.scan(clazz, loader, pkg);
	}
}
