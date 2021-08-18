package net.runeduniverse.libs.rogm.test.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.pipeline.chains.ChainManager;

public class ChainManagerTest {

	public static final String TEST_CHAIN_LABEL = "SYSTEM_TEST";
	public static final String COUNT_TEST_CHAIN_LABEL = "SYSTEM_TEST_COUNT";

	static {
		try {
			Class.forName("net.runeduniverse.libs.rogm.test.model.chains.TestChainContainer");
		} catch (ClassNotFoundException e) {
		}
	}
	
	@Test
	@Tag("system")
	public void test() throws Exception {
		ChainManager.callChain(TEST_CHAIN_LABEL, null);
	}

	@Test
	@Tag("system")
	public void count() throws Exception {
		ChainManager.callChain(COUNT_TEST_CHAIN_LABEL, null);
	}

}
