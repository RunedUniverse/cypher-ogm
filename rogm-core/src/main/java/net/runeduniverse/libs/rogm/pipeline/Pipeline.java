package net.runeduniverse.libs.rogm.pipeline;

import java.util.Map;

public class Pipeline {
	private final IRouter router;
	private final Map<String, IFactory> factories;

	protected Pipeline(final IRouter router, final Map<String, IFactory> factories) {
		this.router = router;
		router.setup(this);
		this.factories = factories;
	}

	public boolean process(Request<?> request) {
		return true;// false => aborted
	}

	public IFactory getFactory(String pipe) {
		return this.factories.get(pipe);
	}
}
