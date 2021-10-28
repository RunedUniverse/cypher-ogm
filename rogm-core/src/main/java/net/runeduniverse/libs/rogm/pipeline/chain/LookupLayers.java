/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.libs.rogm.pipeline.chain;

import java.util.Collection;

import net.runeduniverse.libs.chain.Chain;
import net.runeduniverse.libs.chain.ChainRuntime;
import net.runeduniverse.libs.errors.ExceptionSuppressions;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.Entry;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes.LoadState;
import net.runeduniverse.libs.rogm.lang.DatabaseCleaner;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.lang.Language.IDeleteMapper;
import net.runeduniverse.libs.rogm.lang.Language.ILoadMapper;
import net.runeduniverse.libs.rogm.lang.Language.IMapper;
import net.runeduniverse.libs.rogm.lang.Language.ISaveMapper;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.IRawDataRecord;
import net.runeduniverse.libs.rogm.modules.Module.IRawIdRecord;
import net.runeduniverse.libs.rogm.modules.Module.IRawRecord;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDataRecord;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDeleteContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.SaveContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.UpdatedEntryContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

import static net.runeduniverse.libs.utils.StringUtils.isBlank;

public interface LookupLayers {

	@Chain(label = Chains.DELETE_CHAIN.ONE.LABEL, layers = { Chains.DELETE_CHAIN.ONE.PACKAGE_CONTAINER })
	public static IDeleteContainer packageContainer(final Archive archive, EntityContainer entity, Entry entry)
			throws Exception {
		return archive.delete(entity.getType(), entry.getId(), entity.getEntity());
	}

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = { Chains.LOAD_CHAIN.ALL.BUILD_QUERY_MAPPER })
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.BUILD_QUERY_MAPPER })
	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.BUILD_QUERY_MAPPER })
	@Chain(label = Chains.RELOAD_CHAIN.SELECTED.LABEL, layers = { Chains.RELOAD_CHAIN.SELECTED.BUILD_QUERY_MAPPER })
	public static ILoadMapper buildQryMapper(Language.Instance lang, IFilter filter) throws Exception {
		return lang.load(filter);
	}

	@Chain(label = Chains.SAVE_CHAIN.ONE.LABEL, layers = { Chains.SAVE_CHAIN.ONE.BUILD_QUERY_MAPPER })
	public static ISaveMapper buildSaveMapper(final Archive archive, final IBuffer buffer, final Language.Instance lang,
			SaveContainer container) throws Exception {
		return lang.save(container.getDataContainer(), container.calculateEffectedFilter(archive, buffer));
	}

	@Chain(label = Chains.DELETE_CHAIN.ONE.LABEL, layers = { Chains.DELETE_CHAIN.ONE.BUILD_QUERY_MAPPER })
	public static IDeleteMapper buildDeleteMapper(final Language.Instance lang, IDeleteContainer container)
			throws Exception {
		return lang.delete(container.getDeleteFilter(), container.getEffectedFilter());
	}

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = { Chains.LOAD_CHAIN.ALL.QUERY_DATABASE_FOR_RAW_DATA_RECORD })
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.QUERY_DATABASE_FOR_RAW_DATA_RECORD })
	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.QUERY_DATABASE_FOR_RAW_DATA_RECORD })
	@Chain(label = Chains.RELOAD_CHAIN.SELECTED.LABEL, layers = {
			Chains.RELOAD_CHAIN.SELECTED.QUERY_DATABASE_FOR_RAW_DATA_RECORD })
	public static IRawDataRecord queryDatabase(Module.Instance<?> db, IMapper mapper) {
		return db.queryObject(mapper.qry());
	}

	@Chain(label = Chains.SAVE_CHAIN.ONE.LABEL, layers = { Chains.SAVE_CHAIN.ONE.QUERY_DATABASE_FOR_RAW_ID_RECORD })
	@Chain(label = Chains.DELETE_CHAIN.ONE.LABEL, layers = { Chains.DELETE_CHAIN.ONE.EXECUTE_DELETION_ON_DATABASE })
	public static IRawIdRecord executeQry(Module.Instance<?> db, IMapper mapper) {
		return db.execute(mapper.qry());
	}

	@Chain(label = Chains.DELETE_CHAIN.ONE.LABEL, layers = { Chains.DELETE_CHAIN.ONE.QUERY_DATABASE_FOR_RAW_RECORD })
	public static IRawRecord queryDatabase(final Module.Instance<?> module, IDeleteMapper mapper) {
		return module.query(mapper.effectedQry());
	}

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = {
			Chains.LOAD_CHAIN.ALL.CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD })
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = {
			Chains.LOAD_CHAIN.ONE.CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD })
	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD })
	@Chain(label = Chains.RELOAD_CHAIN.SELECTED.LABEL, layers = {
			Chains.RELOAD_CHAIN.SELECTED.CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD })
	public static IDataRecord convertRecord(ILoadMapper mapper, IRawDataRecord rawDataRecord) {
		return mapper.parseDataRecord(rawDataRecord.getData());
	}

	@Chain(label = Chains.SAVE_CHAIN.ONE.LABEL, layers = { Chains.SAVE_CHAIN.ONE.COLLECT_UPDATED_ENTRIES })
	public static Collection<UpdatedEntryContainer> collectUpdatedEntries(ISaveMapper mapper, IRawIdRecord record,
			DepthContainer depth) {
		return mapper.updateObjectIds(record.getIds(), LoadState.get(depth.getValue() == 0));
	}

	@Chain(label = Chains.SAVE_CHAIN.ONE.LABEL, layers = { Chains.SAVE_CHAIN.ONE.CALL_DATABASE_CLEANUP })
	public static void callDatabaseCleanup(ChainRuntime<?> runtime, DatabaseCleaner cleaner) throws Exception {
		if (!isBlank(cleaner.getChainLabel()))
			runtime.callSubChainWithRuntimeData(cleaner.getChainLabel(), Void.class);
	}

	@Chain(label = Chains.SAVE_CHAIN.ONE.LABEL, layers = { Chains.SAVE_CHAIN.ONE.POST_SAVE_EVENT })
	public static void triggerPostSaveEvent(final Archive archive, final SaveContainer container)
			throws ExceptionSuppressions {
		container.postSave(archive);
	}
}
