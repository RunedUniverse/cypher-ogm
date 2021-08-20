package net.runeduniverse.libs.rogm.pipeline.chain;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.Result;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chain;

public interface BufferLayers extends InternalBufferTypes {
	@SuppressWarnings("deprecation")
	@Chain(label = Chain.BUFFER_LOAD_CHAIN, layers = { 20 })
	public static Object acquireBuffered(final IBuffer buffer, IBaseQueryPattern pattern, IData data,
			LazyEntriesContainer lazyEntries, Result<?> result) throws Exception {
		LoadState loadState = data.getLoadState();
		TypeEntry te = buffer.getTypeEntry(pattern.getType());
		if (te != null) {
			Entry entry = te.getIdEntry(data.getId());
			if (entry != null)
				return result.setResult(LoadState.merge(entry, loadState, lazyEntries));
		}
		return null;
	}

	@Chain(label = Chain.BUFFER_LOAD_CHAIN, layers = { 30 })
	public static EntityContainer parseData(final Parser.Instance parser, IBaseQueryPattern pattern, IData data)
			throws Exception {
		return new EntityContainer(parser.deserialize(pattern.getType(), data.getData()));
	}

	@Chain(label = Chain.BUFFER_LOAD_CHAIN, layers = { 40 })
	public static Object acquireNew(final IBuffer buffer, IBaseQueryPattern pattern, IData data,
			LazyEntriesContainer lazyEntries, EntityContainer container, Result<?> result) throws Exception {
		Object entity = container.getEnitity();
		LoadState loadState = data.getLoadState();
		pattern.setId(entity, data.getEntityId());
		Entry entry = new Entry(data, entity, loadState, pattern);
		if (lazyEntries != null && loadState == LoadState.LAZY)
			lazyEntries.addEntry(entry);
		buffer.addEntry(entry);
		return result.setResult(entity);
	}
}
