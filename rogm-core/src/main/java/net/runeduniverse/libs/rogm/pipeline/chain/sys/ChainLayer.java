package net.runeduniverse.libs.rogm.pipeline.chain.sys;

public final class ChainLayer implements ILayer {

	private final BaseChainLayer base;
	private final boolean ignoreCancelled;

	protected ChainLayer(BaseChainLayer baseLayer, Chain chain) {
		this.base = baseLayer;
		this.ignoreCancelled = chain.ignoreCancelled();
	}

	@Override
	public void call(ChainRuntime<?> runtime) throws Exception {
		this.base.call(runtime);
	}

	public boolean ignoreCancelled() {
		return this.ignoreCancelled;
	}

	public static boolean ignoreCancelled(ILayer layer) {
		return layer instanceof ChainLayer && ((ChainLayer) layer).ignoreCancelled();
	}
}
