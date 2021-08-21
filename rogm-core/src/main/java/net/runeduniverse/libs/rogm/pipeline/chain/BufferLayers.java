package net.runeduniverse.libs.rogm.pipeline.chain;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.IData;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.IdContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chain;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.ChainRuntime;

public interface BufferLayers extends InternalBufferTypes {

	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.CHECK_BUFFERED_STATUS })
	public static <T> void ckeckBufferedStatus(final ChainRuntime<T> runtime, final IBuffer buffer, IdContainer id,
			DepthContainer depth) {
		if (id == null)
			return;
		T o;
		if (depth.getDepth() == 0)
			o = buffer.getByEntityId(id.getId(), runtime.getResultType());
		else
			o = buffer.getCompleteByEntityId(id.getId(), runtime.getResultType());
		if (o != null)
			runtime.setResult(o);
	}

	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.PREPARE_DATA })
	public static void prepareDataForBuffer(IBaseQueryPattern pattern, IData data) {
		pattern.prepareEntityId(data);
	}

	@SuppressWarnings("deprecation")
	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.ACQUIRE_BUFFERED_ENTITY })
	public static Object acquireBuffered(final ChainRuntime<?> runtime, final IBuffer buffer, IBaseQueryPattern pattern,
			IData data, LazyEntriesContainer lazyEntries) throws Exception {
		LoadState loadState = data.getLoadState();
		TypeEntry te = buffer.getTypeEntry(pattern.getType());
		if (te != null) {
			Entry entry = te.getIdEntry(data.getId());
			if (entry != null) {
				Object entity = LoadState.merge(entry, loadState, lazyEntries);
				runtime.setPossibleResult(entity);
				return entity;
			}
		}
		return null;
	}

	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.DESERIALIZE_DATA })
	public static EntityContainer parseData(final Parser.Instance parser, IBaseQueryPattern pattern, IData data)
			throws Exception {
		return new EntityContainer(parser.deserialize(pattern.getType(), data.getData()));
	}

	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.ACQUIRE_NEW_ENTITY })
	public static Object acquireNew(final ChainRuntime<?> runtime, final IBuffer buffer, IBaseQueryPattern pattern,
			IData data, LazyEntriesContainer lazyEntries, EntityContainer container) throws Exception {
		Object entity = container.getEntity();
		LoadState loadState = data.getLoadState();
		pattern.setId(entity, data.getEntityId());
		Entry entry = new Entry(data, entity, loadState, pattern);
		if (lazyEntries != null && loadState == LoadState.LAZY)
			lazyEntries.addEntry(entry);
		buffer.addEntry(entry);

		runtime.setPossibleResult(entity);
		return entity;
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
	public static Entry updateBufferedEntry(final ChainRuntime<Entry> runtime, final IBuffer buffer,
			EntityContainer entityContainer, IData data) throws Exception {
		Object entity = entityContainer.getEntity();
		Entry entry = buffer.getEntry(entity);

		buffer.updateEntry(entry, data.getId(), data.getEntityId());
		entry.getPattern()
				.setId(entity, data.getEntityId());
		runtime.setResult(entry);
		return entry;
	}
}
