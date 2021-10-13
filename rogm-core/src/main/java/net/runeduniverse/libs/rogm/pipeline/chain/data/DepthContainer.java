package net.runeduniverse.libs.rogm.pipeline.chain.data;

import lombok.ToString;

@ToString
public class DepthContainer {
	private int depth;

	public DepthContainer(int depth) {
		this.set(depth);
	}

	public int getValue() {
		return this.depth;
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
