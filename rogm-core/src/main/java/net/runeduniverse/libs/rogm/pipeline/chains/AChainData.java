package net.runeduniverse.libs.rogm.pipeline.chains;

import lombok.Getter;
import net.runeduniverse.libs.rogm.pipeline.Pipeline;
import net.runeduniverse.libs.rogm.pipeline.Pipeline.ChainData;

@Getter
public abstract class AChainData implements ChainData {

	protected final Pipeline pipeline;

	public AChainData(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public void jumpToLayer(int nextLayer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
