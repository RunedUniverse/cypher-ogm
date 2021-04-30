package net.runeduniverse.libs.rogm.pipeline;

import java.lang.annotation.Annotation;

import net.runeduniverse.libs.rogm.pipeline.chains.LoadChain;

public class EntityPipeline implements Pipeline {

	public EntityPipeline() {
		this.registerChain(Chain.Load.LABEL, LoadChain.Data.class);
		this.registerChain(Chain.Reload.LABEL, null);
		this.registerChain(Chain.Save.LABEL, null);
		this.registerChain(Chain.Delete.LABEL, null);
	}

	@Override
	public <A extends Annotation, D extends ChainData> void registerChain(String label, Class<D> dataClass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerChainProcessor(Object processor) {
		// TODO Auto-generated method stub
		
	}

}
