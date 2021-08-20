package net.runeduniverse.libs.rogm.pipeline.chain.sys;

public class ChainRuntime {

	final ChainRuntime root;

	protected ChainRuntime(Object[] args) {
		this.root = null;
	}

	protected ChainRuntime(ChainRuntime root, Object[] args) {
		this.root = root;
	}

	public boolean isRoot() {
		return this.root == null;
	}
}
