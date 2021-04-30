package net.runeduniverse.libs.rogm.pipeline;

import java.lang.annotation.Annotation;

public interface Pipeline {

	<A extends Annotation, D extends ChainData> void registerChain(String label, Class<D> dataClass);

	void registerChainProcessor(Object processor);

	public interface ChainData {
		void jumpToLayer(int nextLayer);

		void commit();

		void cancel();

		Pipeline getPipeline();
	}
}
