package net.runeduniverse.libs.rogm.test.model.chains;

import net.runeduniverse.libs.rogm.pipeline.chains.Chain;
import net.runeduniverse.libs.rogm.test.system.ChainManagerTest;

public interface TestChainContainer {

	@Chain(label = ChainManagerTest.TEST_CHAIN_LABEL, layers = { 100 })
	public static <T> T test(Class<T> type) throws InstantiationException, IllegalAccessException {
		System.out.println(type.getCanonicalName());
		return type.newInstance();
	}

	@Chain(label = ChainManagerTest.COUNT_TEST_CHAIN_LABEL, layers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 })
	public static Number printCount(Integer number) {
		if (number == null)
			number = 0;
		System.out.println(number);
		return number + 1;
	}

	@Chain(label = ChainManagerTest.PRINT_A_TEST_CHAIN_LABEL, layers = { 200 })
	@Chain(label = ChainManagerTest.PRINT_B_TEST_CHAIN_LABEL, layers = { 400 })
	public static void printString(String str) {
		System.out.println(str);
	}
}
