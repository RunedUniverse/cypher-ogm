package net.runeduniverse.libs.rogm.pipeline.chain.data;

import lombok.Getter;

public class DepthContainer {
	@Getter
	private int depth;

	public DepthContainer(int depth) {
		this.set(depth);
	}

	public void set(int value) {
		if (depth < 0)
			this.depth = 0;
		else
			this.depth = value;
	}

	public void subtractOne() {
		this.depth = this.depth - 1;
	}
}
