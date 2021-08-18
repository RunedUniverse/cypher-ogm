package net.runeduniverse.libs.rogm.test.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.pipeline.chains.ChainManager;
import net.runeduniverse.libs.rogm.test.model.Player;
import net.runeduniverse.libs.rogm.test.model.chains.TestChainContainer;

public class ChainManagerTest {

	public static final String TEST_CHAIN_LABEL = "SYSTEM_TEST";
	public static final String COUNT_TEST_CHAIN_LABEL = "SYSTEM_TEST_COUNT";
	public static final String PRINT_A_TEST_CHAIN_LABEL = "SYSTEM_TEST_PRINT_A";
	public static final String PRINT_B_TEST_CHAIN_LABEL = "SYSTEM_TEST_PRINT_B";

	static {
		ChainManager.addChainLayers(TestChainContainer.class);
	}

	@Test
	@Tag("system")
	public void test() throws Exception {
		Class<?> type = Player.class;
		ChainManager.callChain(TEST_CHAIN_LABEL, type, type);
	}

	@Test
	@Tag("system")
	public void count() throws Exception {
		ChainManager.callChain(COUNT_TEST_CHAIN_LABEL, null);
	}

	@Test
	@Tag("system")
	public void print() throws Exception {
		ChainManager.callChain(PRINT_A_TEST_CHAIN_LABEL, null, "A");
		ChainManager.callChain(PRINT_B_TEST_CHAIN_LABEL, null, "B");
	}

}
