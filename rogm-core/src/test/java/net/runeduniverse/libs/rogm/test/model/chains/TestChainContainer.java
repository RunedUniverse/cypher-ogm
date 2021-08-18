package net.runeduniverse.libs.rogm.test.model.chains;

import net.runeduniverse.libs.rogm.pipeline.chains.Chain;
import net.runeduniverse.libs.rogm.pipeline.chains.ChainManager;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.test.system.ChainManagerTest;

public class TestChainContainer {

	static {
		ChainManager.addChainLayers(TestChainContainer.class);
	}

	@Chain(label = ChainManagerTest.TEST_CHAIN_LABEL, layers = { 100 })
	public static Object test(IFilter filter) {
		return filter;
	}

	@Chain(label = ChainManagerTest.COUNT_TEST_CHAIN_LABEL, layers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 })
	public static Number printCount(Integer number) {
		if (number == null)
			number = 0;
		System.out.println(number);
		return number+1;
	}
}
