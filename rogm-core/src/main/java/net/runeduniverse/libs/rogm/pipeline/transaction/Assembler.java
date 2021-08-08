package net.runeduniverse.libs.rogm.pipeline.transaction;

import java.nio.Buffer;

import net.runeduniverse.libs.rogm.pattern.Archive;

public final class Assembler implements ILayer{
	private final Archive archive;
	private final Buffer buffer;

	public Assembler(Archive archive, Buffer buffer) {
		this.archive = archive;
		this.buffer = buffer;
	}

}
