package net.runeduniverse.libs.rogm.pipeline;

import java.util.Map;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.error.ScannerException;
import net.runeduniverse.libs.rogm.pattern.Archive;

public class Pipeline {
	private final Configuration cnf;
	private final Archive archive;
	private final IRouter router;
	private final Map<String, IFactory> factories;

	protected Pipeline(final Configuration cnf, final IRouter router, final Map<String, IFactory> factories) {
		this.cnf = cnf;
		this.archive = new Archive(this.cnf);
		this.router = router;
		router.setup(this);
		this.factories = factories;
	}
	
	public Pipeline setup() throws ScannerException {
		this.archive.applyConfig();
		return this;
	}

	public boolean process(Request<?> request) {
		return true;// false => aborted
	}

	public IFactory getFactory(String pipe) {
		return this.factories.get(pipe);
	}
}
