package net.runeduniverse.libs.rogm.pipeline.chain;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chain;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainRuntime;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Result;

public interface BufferLayers extends InternalBufferTypes {

	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.PREPARE_DATA })
	public static void prepareDataForBuffer(IBaseQueryPattern pattern, IData data) {
		pattern.prepareEntityId(data);
	}

	@SuppressWarnings("deprecation")
	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.ACQUIRE_BUFFERED_ENTITY })
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

	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.DESERIALIZE_DATA })
	public static EntityContainer parseData(final Parser.Instance parser, IBaseQueryPattern pattern, IData data)
			throws Exception {
		return new EntityContainer(parser.deserialize(pattern.getType(), data.getData()));
	}

	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.ACQUIRE_NEW_ENTITY })
	public static Object acquireNew(final IBuffer buffer, IBaseQueryPattern pattern, IData data,
			LazyEntriesContainer lazyEntries, EntityContainer container, Result<?> result) throws Exception {
		Object entity = container.getEntity();
		LoadState loadState = data.getLoadState();
		pattern.setId(entity, data.getEntityId());
		Entry entry = new Entry(data, entity, loadState, pattern);
		if (lazyEntries != null && loadState == LoadState.LAZY)
			lazyEntries.addEntry(entry);
		buffer.addEntry(entry);
		return result.setResult(entity);
	}

	@Chain(label = Chains.BUFFER_CHAIN.UPDATE.LABEL, layers = { Chains.BUFFER_CHAIN.UPDATE.PREPARE_DATA })
	public static EntityContainer prepareDataReloadForBuffer(final IBuffer buffer, IBaseQueryPattern pattern,
			IData data) {
		return new EntityContainer(pattern.prepareEntityUpdate(buffer, data));
	}

	@Chain(label = Chains.BUFFER_CHAIN.UPDATE.LABEL, layers = { Chains.BUFFER_CHAIN.UPDATE.VALIDATE_ENTITY })
	public static void validateUpdate(final ChainRuntime<?> runtime, EntityContainer entityContainer) throws Exception {
		if (entityContainer.getEntity() == null)
			runtime.setCanceled(true);
	}

	@Chain(label = Chains.BUFFER_CHAIN.UPDATE.LABEL, layers = { Chains.BUFFER_CHAIN.UPDATE.DESERIALIZE_DATA })
	public static void parseDataToEntityContainerRef(final Parser.Instance parser,
			final EntityContainer entityContainer, IData data) throws Exception {
		parser.deserialize(entityContainer.getEntity(), data.getData());
	}

	@SuppressWarnings("deprecation")
	@Chain(label = Chains.BUFFER_CHAIN.UPDATE.LABEL, layers = { Chains.BUFFER_CHAIN.UPDATE.UPDATE_BUFFERED_ENTRY })
	public static Entry updateBufferedEntry(final IBuffer buffer, final Result<Entry> result,
			EntityContainer entityContainer, IData data) throws Exception {
		Object entity = entityContainer.getEntity();
		Entry entry = buffer.getEntry(entity);

		buffer.updateEntry(entry, data.getId(), data.getEntityId());
		entry.getPattern()
				.setId(entity, data.getEntityId());
		result.setResult(entry);
		return entry;
	}
}
