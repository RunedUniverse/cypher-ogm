package net.runeduniverse.libs.rogm.pipeline;

import java.lang.annotation.Annotation;
import java.util.Comparator;

public interface Pipeline {

	<A extends Annotation, D> void registerChain(Class<A> anno, Class<D> dataClass, Comparator<A> c);

	default <A extends Annotation, D> void registerChain(Class<A> anno, Class<D> dataClass) {
		this.registerChain(anno, dataClass, new Comparator<A>() {

			@Override
			public int compare(A o1, A o2) {
				return 0;
			}
		});
	}
}
