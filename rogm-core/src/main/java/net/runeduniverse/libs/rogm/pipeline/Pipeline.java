package net.runeduniverse.libs.rogm.pipeline;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pipeline.transaction.Assembler;

public class Pipeline {
	private final Configuration cnf;
	private final Archive archive;
	
	private Assembler assembler = null;

	protected Pipeline(final Configuration cnf) {
		this.cnf = cnf;
		this.archive = new Archive(this.cnf.getModelInfo());
	}
	
	public Pipeline setup() throws ScannerException {
		this.archive.applyConfig();
		//this.assembler = new Assembler(this.archive);
		return this;
	}
}
