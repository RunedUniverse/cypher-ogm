/*
 * Copyright © 2023 VenaNocta (venanocta@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.rogm.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.runeduniverse.lib.rogm.lang.Language.IDeleteMapper;
import net.runeduniverse.lib.rogm.modules.Module.IRawRecord;
import net.runeduniverse.lib.rogm.parser.Parser;
import net.runeduniverse.lib.rogm.pattern.Archive;
import net.runeduniverse.lib.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.lib.rogm.pattern.IPattern.IData;
import net.runeduniverse.lib.rogm.pattern.IPattern.IDeleteContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.Chains;
import net.runeduniverse.lib.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.IdContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.lib.rogm.pipeline.chain.data.UpdatedEntryContainer;
import net.runeduniverse.lib.utils.chain.Chain;
import net.runeduniverse.lib.utils.chain.ChainRuntime;
import net.runeduniverse.lib.utils.errors.ExceptionSuppressions;
import net.runeduniverse.lib.utils.logging.UniversalLogger;

public interface BasicBufferLayers extends InternalBufferTypes {

	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.CHECK_BUFFERED_STATUS })
	public static <T> void ckeckBufferedStatus(final ChainRuntime<T> runtime, final IBuffer buffer, IdContainer id,
			DepthContainer depth) {
		if (id == null || id.getId() == null)
			return;
		T o;
		if (depth.getValue() == 0)
			o = buffer.getByEntityId(id.getId(), runtime.getResultType());
		else
			o = buffer.getCompleteByEntityId(id.getId(), runtime.getResultType());
		if (o != null)
			runtime.setResult(o);
	}

	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.PREPARE_DATA })
	public static void prepareDataForBuffer(IBaseQueryPattern<?> pattern, IData data) {
		pattern.prepareEntityId(data);
	}

	@SuppressWarnings("deprecation")
	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.ACQUIRE_BUFFERED_ENTITY })
	public static Object acquireBuffered(final ChainRuntime<?> runtime, final BasicBuffer buffer,
			IBaseQueryPattern<?> pattern, IData data, LazyEntriesContainer lazyEntries) throws Exception {
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
	public static EntityContainer parseData(final Parser.Instance parser, IBaseQueryPattern<?> pattern, IData data)
			throws Exception {
		return new EntityContainer(parser.deserialize(pattern.getType(), data.getData()));
	}

	@Chain(label = Chains.BUFFER_CHAIN.LOAD.LABEL, layers = { Chains.BUFFER_CHAIN.LOAD.ACQUIRE_NEW_ENTITY })
	public static Object acquireNew(final ChainRuntime<?> runtime, final IBuffer buffer, final UniversalLogger logger,
			IBaseQueryPattern<?> pattern, IData data, LazyEntriesContainer lazyEntries, EntityContainer container)
			throws Exception {
		logger.finest("acquireNewObject for " + data);
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

	@Chain(label = Chains.DELETE_CHAIN.ONE.LABEL, layers = { Chains.DELETE_CHAIN.ONE.GET_BUFFERED_ENTRY })
	public static IEntry getBufferedEntry(final ChainRuntime<?> runtime, final IBuffer buffer,
			final EntityContainer entity) throws Exception {
		IEntry entry = buffer.getEntry(entity.getEntity());
		if (entry == null)
			throw new Exception("Entity of type<" + entity.getType()
					.getName() + "> is not loaded!");
		return entry;
	}

	@Chain(label = Chains.BUFFER_CHAIN.UPDATE.LABEL, layers = { Chains.BUFFER_CHAIN.UPDATE.PREPARE_DATA })
	public static EntityContainer prepareDataReloadForBuffer(final IBuffer buffer, IBaseQueryPattern<?> pattern,
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
	public static Entry updateBufferedEntry(final ChainRuntime<Entry> runtime, final BasicBuffer buffer,
			EntityContainer entityContainer, IData data) throws Exception {
		Object entity = entityContainer.getEntity();
		Entry entry = buffer.getEntry(entity);

		buffer.updateEntry(entry, data.getId(), data.getEntityId());
		entry.getPattern()
				.setId(entity, data.getEntityId());
		runtime.setResult(entry);
		return entry;
	}

	@Chain(label = Chains.SAVE_CHAIN.ONE.LABEL, layers = { Chains.SAVE_CHAIN.ONE.UPDATE_BUFFER_ENTRIES })
	public static void updateBufferedEntries(final Archive archive, final IBuffer buffer,
			Collection<UpdatedEntryContainer> collection) throws ExceptionSuppressions {
		List<Exception> errors = new ArrayList<>();
		for (UpdatedEntryContainer updatedEntry : collection) {
			try {
				buffer.updateEntry(archive, updatedEntry);
			} catch (Exception e) {
				errors.add(e);
			}
		}
		if (!errors.isEmpty())
			throw new ExceptionSuppressions("Surpressed Exceptions while updating buffered Ids", true)
					.addSuppressed(errors);
	}

	@Chain(label = Chains.DELETE_CHAIN.ONE.LABEL, layers = { Chains.DELETE_CHAIN.ONE.UPDATE_BUFFER })
	public static void updateBuffer(final IBuffer buffer, IDeleteMapper mapper, IDeleteContainer container,
			IRawRecord record) {
		mapper.updateBuffer(buffer, container.getDeletedId(), record.getRawData());
	}

}
