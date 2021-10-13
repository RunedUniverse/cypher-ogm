package net.runeduniverse.libs.rogm.modules.neo4j;

import java.util.Collection;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.error.ExceptionSuppressions;
import net.runeduniverse.libs.rogm.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pipeline.chain.Chains;
import net.runeduniverse.libs.rogm.pipeline.chain.data.UpdatedEntryContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chain;

public interface DebugChainLayers {

	@Chain(label = Chains.SAVE_CHAIN.ONE.LABEL, layers = { Chains.SAVE_CHAIN.ONE.UPDATE_BUFFER_ENTRIES + 1 })
	public static void debugUpdateBufferedEntries(final Archive archive, final IBuffer buffer,
			final UniversalLogger logger, Collection<UpdatedEntryContainer> collection) throws ExceptionSuppressions {
		for (UpdatedEntryContainer updatedEntry : collection) {
			logger.finer(updatedEntry.toString());
		}
	}
}
