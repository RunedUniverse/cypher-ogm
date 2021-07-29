package net.runeduniverse.libs.rogm.pipeline;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.pattern.Archive;

public class Pipeline {
	private final Configuration cnf;
	private final Archive archive;
	private final IRouter router;
	
	private Assembler assembler = null;

	protected Pipeline(final Configuration cnf, final IRouter router) {
		this.cnf = cnf;
		this.archive = new Archive(this.cnf);
		this.router = router;
		router.setup(this);
	}
	
	public Pipeline setup() throws ScannerException {
		this.archive.applyConfig();
		this.assembler = new Assembler(this.archive);
		return this;
	}

	public boolean process(Request<?> request) {
		return true;// false => aborted
	}
}
