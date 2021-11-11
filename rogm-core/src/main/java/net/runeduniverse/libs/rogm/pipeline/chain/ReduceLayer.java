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
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.BufferTypes.IEntry;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pipeline.chain.data.DepthContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.EntityCollectionContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.LazyEntriesContainer;
import net.runeduniverse.libs.rogm.pipeline.chain.data.RelatedEntriesContainer;

public interface ReduceLayer {

	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = {
			Chains.LOAD_CHAIN.ONE.REDUCE_COLLECTION }, ignoreResult = true)
	public static <T> T reduceCollection(final ChainRuntime<T> runtime, Collection<T> collection) {
		for (T t : collection) {
			runtime.setResult(t);
			return t;
		}
		return null;
	}

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = { Chains.LOAD_CHAIN.ALL.RESOLVE_DEPTH }, ignoreResult = true)
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.RESOLVE_DEPTH }, ignoreResult = true)
	public static void resolveDepth(final ChainRuntime<?> runtime, DepthContainer depth) throws Exception {
		if (depth.getValue() < 2)
			return;
		depth.subtractOne();
		runtime.callSubChainWithSourceData(Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, null);
	}

	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.VALIDATE_LAZY_ENTRIES })
	public static void validateLazyEntries(final ChainRuntime<?> runtime, final LazyEntriesContainer lazyEntries,
			final DepthContainer depth) {
		if (lazyEntries.isEmpty() || depth == null || depth.getValue() <= 0)
			runtime.setCanceled(true);
	}

	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.CALL_RESOLVE_SELECTED })
	public static void resolveLazyEntries(final ChainRuntime<?> runtime, final Archive archive,
			final LazyEntriesContainer lazyEntries) throws Exception {
		LazyEntriesContainer nextLazyEntries = new LazyEntriesContainer();
		for (IEntry entry : lazyEntries.getLazyEntries()) {
			runtime.callSubChainWithSourceData(Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL, Collection.class,
					archive.search(entry.getType(), entry.getId(), false)
							.getResult(),
					nextLazyEntries);
		}
		lazyEntries.clear();
		lazyEntries.addEntries(nextLazyEntries);
	}

	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.LOOP_LAZY_ENTRIES })
	public static void loopLazyEntries(final ChainRuntime<?> runtime, final DepthContainer depth) {
		depth.subtractOne();
		if (0 < depth.getValue())
			runtime.jumpToLayer(Chains.LOAD_CHAIN.RESOLVE_LAZY.ALL.VALIDATE_LAZY_ENTRIES);
	}

	@Chain(label = Chains.RELOAD_CHAIN.ALL.LABEL, layers = { Chains.RELOAD_CHAIN.ALL.CALL_RELOAD_SELECTED })
	public static void reloadEntries(final ChainRuntime<?> runtime, final Archive archive, final IBuffer buffer,
			EntityCollectionContainer entityCollection, DepthContainer depth) throws Exception {
		RelatedEntriesContainer relatedEntries = runtime.storeData(new RelatedEntriesContainer());
		for (Object entity : entityCollection.getEntityCollection()) {
			IEntry entry = buffer.getEntry(entity);
			if (entry != null)
				runtime.callSubChainWithSourceData(Chains.RELOAD_CHAIN.SELECTED.LABEL, Void.class, relatedEntries,
						archive.search(entry.getType(), entry.getId(), depth.getValue() == 0)
								.getResult());
		}
		depth.subtractOne();
	}

	@Chain(label = Chains.RELOAD_CHAIN.ALL.LABEL, layers = { Chains.RELOAD_CHAIN.ALL.VALIDATE_RELATED_ENTRIES })
	public static void validateRelatedEntries(final ChainRuntime<?> runtime, final DepthContainer depth,
			RelatedEntriesContainer relatedEntries) {
		if (relatedEntries.isEmpty() || depth == null || depth.getValue() <= 0)
			runtime.setCanceled(true);
	}

	@Chain(label = Chains.RELOAD_CHAIN.ALL.LABEL, layers = { Chains.RELOAD_CHAIN.ALL.CALL_RELOAD_SELECTED_FOR_RELATED })
	public static void reloadRelatedEntries(final ChainRuntime<?> runtime, final Archive archive, final IBuffer buffer,
			RelatedEntriesContainer relatedEntries, DepthContainer depth) throws Exception {
		RelatedEntriesContainer nextRelatedEntries = new RelatedEntriesContainer();
		for (IEntry entry : relatedEntries.getRelatedEntries()) {
			if (entry != null)
				runtime.callSubChainWithSourceData(Chains.RELOAD_CHAIN.SELECTED.LABEL, Void.class, nextRelatedEntries,
						archive.search(entry.getType(), entry.getId(), depth.getValue() == 0)
								.getResult());
		}
		relatedEntries.clear();
		relatedEntries.addEntries(nextRelatedEntries);
	}

	@Chain(label = Chains.RELOAD_CHAIN.ALL.LABEL, layers = { Chains.RELOAD_CHAIN.ALL.LOOP_RELATED_ENTRIES })
	public static void loopRelatedEntries(final ChainRuntime<?> runtime, final DepthContainer depth) {
		depth.subtractOne();
		if (0 < depth.getValue())
			runtime.jumpToLayer(Chains.RELOAD_CHAIN.ALL.VALIDATE_RELATED_ENTRIES);
	}
}
