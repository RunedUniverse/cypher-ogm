package net.runeduniverse.libs.rogm.pipeline;

import java.lang.annotation.Annotation;
import java.util.Comparator;

import net.runeduniverse.libs.rogm.pipeline.chains.DeleteChain;
import net.runeduniverse.libs.rogm.pipeline.chains.LoadChain;
import net.runeduniverse.libs.rogm.pipeline.chains.ReloadChain;
import net.runeduniverse.libs.rogm.pipeline.chains.SaveChain;

public class EntityPipeline implements Pipeline {

	public EntityPipeline() {
		this.registerChain(LoadChain.class, null);
		this.registerChain(ReloadChain.class, null);
		this.registerChain(SaveChain.class, null);
		this.registerChain(DeleteChain.class, null);
	}

	@Override
	public <A extends Annotation, D> void registerChain(Class<A> anno, Class<D> dataClass, Comparator<A> a) {
		// TODO Auto-generated method stub
		
	}

}
