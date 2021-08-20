package net.runeduniverse.libs.rogm.pipeline.chain.sys;

public interface ILayer {
	void call(ChainRuntime<?> runtime) throws Exception;
}
