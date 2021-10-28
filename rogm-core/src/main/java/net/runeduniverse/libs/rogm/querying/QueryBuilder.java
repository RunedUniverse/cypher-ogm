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
package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.runeduniverse.libs.logging.logs.CompoundTree;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.modules.IdTypeResolver;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.INodePattern;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IRelationPattern;
import net.runeduniverse.libs.rogm.querying.builder.AProxyFilter;
import net.runeduniverse.libs.rogm.querying.builder.DataContainerHandler;
import net.runeduniverse.libs.rogm.querying.builder.ITraceable;
import net.runeduniverse.libs.rogm.querying.builder.IdentifiedHandler;
import net.runeduniverse.libs.rogm.querying.builder.LazyLoadingHandler;
import net.runeduniverse.libs.rogm.querying.builder.NodeFilter;
import net.runeduniverse.libs.rogm.querying.builder.OptionalHandler;
import net.runeduniverse.libs.rogm.querying.builder.ParamHandler;
import net.runeduniverse.libs.rogm.querying.builder.PatternContainerHandler;
import net.runeduniverse.libs.rogm.querying.builder.RelationFilter;
import net.runeduniverse.libs.rogm.querying.builder.ReturnedHandler;
import net.runeduniverse.libs.utils.StringVariableGenerator;

public final class QueryBuilder {
	public static creator<NodeQueryBuilder> CREATOR_NODE_BUILDER = QueryBuilder::createNodeBuilder;
	public static creator<RelationQueryBuilder> CREATOR_REALATION_BUILDER = QueryBuilder::createRelationBuilder;
	private final Archive archive;

	public QueryBuilder(Archive archive) {
		this.archive = archive;
	}

	public NodeQueryBuilder node() {
		return CREATOR_NODE_BUILDER.create(this.archive);
	}

	public RelationQueryBuilder relation() {
		return CREATOR_REALATION_BUILDER.create(this.archive);
	}

	@FunctionalInterface
	public static interface creator<BUILDER> {
		BUILDER create(Archive archive);
	}

	private static NodeQueryBuilder createNodeBuilder(Archive archive) {
		return new NodeQueryBuilder(archive);
	}

	private static RelationQueryBuilder createRelationBuilder(Archive archive) {
		return new RelationQueryBuilder(archive);
	}

	public static class NodeQueryBuilder
			extends AQueryBuilder<NodeQueryBuilder, NodeFilter, INodePattern<NodeQueryBuilder>, IFNode> {

		protected Set<RelationQueryBuilder> relationBuilders = new HashSet<>();

		public NodeQueryBuilder(Archive archive) {
			super(archive, new NodeFilter());
			super.instance = this;
		}

		@Deprecated
		public NodeQueryBuilder addRelation(RelationQueryBuilder relation) {
			this.relationBuilders.add(relation);
			return this;
		}

		public NodeQueryBuilder addRelationTo(NodeQueryBuilder target) {
			super.archive.getQueryBuilder()
					.relation()
					.setStart(this)
					.setTarget(target);
			return this;
		}

		public NodeQueryBuilder addRelationTo(RelationQueryBuilder relation, NodeQueryBuilder target) {
			relation.setStart(this)
					.setTarget(target);
			return this;
		}

		public NodeQueryBuilder addRelationFrom(NodeQueryBuilder start) {
			super.archive.getQueryBuilder()
					.relation()
					.setStart(start)
					.setTarget(this);
			return this;
		}

		public NodeQueryBuilder addRelationFrom(RelationQueryBuilder relation, NodeQueryBuilder start) {
			relation.setStart(start)
					.setTarget(this);
			return this;
		}

		public NodeQueryBuilder setLazy(boolean lazy) {
			if (lazy)
				this.handler.put(ILazyLoading.class, new LazyLoadingHandler(lazy));
			else
				this.handler.remove(ILazyLoading.class);
			return super.setLazy(lazy);
		}

		@Override
		protected void validateStructure() {
			if (this.valid)
				return;
			this.valid = true;

			for (RelationQueryBuilder builder : this.relationBuilders)
				builder.validateStructure();

			FilterType thisFilterType = this.proxyFilter.getFilterType();

			if ((thisFilterType == FilterType.MATCH || thisFilterType == FilterType.DELETE) && this.type != null
					&& this.isReturned() && !this.isAutoGenerated())
				try {
					this.getStoredPattern()
							.completeSearch(this);
				} catch (Exception e) {
					e.printStackTrace();
				}

		}

		protected IFNode build(final Map<IQueryBuilder<?, ?, ?>, IFilter> registry) {
			if (registry.containsKey(this))
				return (IFNode) registry.get(this);

			super.prebuild();

			registry.put(this, super.result = (IFNode) Proxy.newProxyInstance(QueryBuilder.class.getClassLoader(),
					this.proxyFilter.buildInvocationHandler(), this.proxyFilter));

			// resolve relations AFTER the registry entry got created!!!
			for (RelationQueryBuilder rqb : this.relationBuilders)
				this.proxyFilter.getRelations()
						.add(rqb.build(registry));

			return super.result;
		}

		@Override
		protected void toRecord(CompoundTree tree, StringVariableGenerator gen,
				Map<IQueryBuilder<?, ?, ?>, String> registry, CompoundTree localTree) {
			for (RelationQueryBuilder rqb : this.relationBuilders)
				localTree.append("REL", rqb.toRecord2(tree, gen, registry));
		}
	}

	public static class RelationQueryBuilder extends
			AQueryBuilder<RelationQueryBuilder, RelationFilter, IRelationPattern<RelationQueryBuilder>, IFRelation> {

		protected Direction direction = Direction.BIDIRECTIONAL;
		protected NodeQueryBuilder startNodeBuilder = null;
		protected NodeQueryBuilder targetNodeBuilder = null;

		public RelationQueryBuilder(Archive archive) {
			super(archive, new RelationFilter());
			super.instance = this;
		}

		public RelationQueryBuilder whereDirection(Direction direction) {
			this.direction = direction == null ? Direction.BIDIRECTIONAL : direction;
			return this;
		}

		public RelationQueryBuilder setStart(NodeQueryBuilder start) {
			this.startNodeBuilder = start.addRelation(this);
			return this;
		}

		public RelationQueryBuilder setTarget(NodeQueryBuilder target) {
			this.targetNodeBuilder = target.addRelation(this);
			return this;
		}

		public NodeQueryBuilder getStart() {
			return this.startNodeBuilder;
		}

		public NodeQueryBuilder getTarget() {
			return this.targetNodeBuilder;
		}

		protected IFRelation build(final Map<IQueryBuilder<?, ?, ?>, IFilter> registry) {
			if (registry.containsKey(this))
				return (IFRelation) registry.get(this);

			super.prebuild();
			this.proxyFilter.setDirection(this.direction);

			registry.put(this, super.result = (IFRelation) Proxy.newProxyInstance(QueryBuilder.class.getClassLoader(),
					this.proxyFilter.buildInvocationHandler(), this.proxyFilter));

			// resolve relations AFTER the registry entry got created!!!
			this.proxyFilter.setStart(this.startNodeBuilder == null ? null : this.startNodeBuilder.build(registry));
			this.proxyFilter.setTarget(this.targetNodeBuilder == null ? null : this.targetNodeBuilder.build(registry));

			return super.result;
		}

		@Override
		protected void validateStructure() {
			if (this.valid)
				return;
			this.valid = true;

			FilterType thisFilterType = this.proxyFilter.getFilterType();
			boolean isNotCreate = true;

			if (this.startNodeBuilder != null) {
				this.startNodeBuilder.validateStructure();
				if (this.startNodeBuilder.proxyFilter.getFilterType() == FilterType.CREATE) {
					if (thisFilterType != FilterType.CREATE)
						this.asUpdate();
					isNotCreate = false;
				}
			}

			if (this.targetNodeBuilder != null) {
				this.targetNodeBuilder.validateStructure();
				if (this.targetNodeBuilder.proxyFilter.getFilterType() == FilterType.CREATE) {
					if (thisFilterType != FilterType.CREATE)
						this.asUpdate();
					isNotCreate = false;
				}
			}

			if (isNotCreate && this.type != null && this.isReturned() && !this.isAutoGenerated())
				try {
					this.getStoredPattern()
							.completeSearch(this);
				} catch (Exception e) {
					e.printStackTrace();
				}

		}

		@Override
		protected void toRecord(CompoundTree tree, StringVariableGenerator gen,
				Map<IQueryBuilder<?, ?, ?>, String> registry, CompoundTree localTree) {
			this.startNodeBuilder.toRecord(tree, gen, registry);
			this.targetNodeBuilder.toRecord(tree, gen, registry);
			localTree.append("DIRECTION", this.direction.name());
			localTree.append("START", "(" + registry.get(this.startNodeBuilder) + ")");
			localTree.append("TARGET", "(" + registry.get(this.targetNodeBuilder) + ")");
		}

		public CharSequence toRecord2(CompoundTree tree, StringVariableGenerator gen,
				Map<IQueryBuilder<?, ?, ?>, String> registry) {
			super.toRecord(tree, gen, registry);

			return String.format("(%s) %s-[%s]-%s (%s)",
					this.startNodeBuilder == null ? "" : registry.get(this.startNodeBuilder),
					this.direction == Direction.OUTGOING ? "-" : "<", registry.get(this),
					this.direction == Direction.INCOMING ? "-" : ">",
					this.targetNodeBuilder == null ? "" : registry.get(this.targetNodeBuilder));
		}
	}

	protected static abstract class AQueryBuilder<BUILDER extends AQueryBuilder<?, PROXY_FILTER, PATTERN, RESULT>, PROXY_FILTER extends AProxyFilter<?>, PATTERN extends IBaseQueryPattern<?>, RESULT extends IFilter>
			implements IQueryBuilder<BUILDER, PATTERN, RESULT> {

		protected final Archive archive;
		protected final PROXY_FILTER proxyFilter;
		protected final Map<Class<?>, Object> handler;
		protected boolean autoGenerated = false;
		protected boolean valid = false;

		// BUILDER instance:
		// must be configured in child-constructor for chaining to work!
		protected BUILDER instance;
		protected RESULT result = null;
		protected Class<?> type = null;
		protected Serializable id = null;
		protected boolean lazy = false;

		protected AQueryBuilder(Archive archive, PROXY_FILTER proxyFilter) {
			this.archive = archive;
			this.proxyFilter = proxyFilter;
			this.handler = this.proxyFilter.getHandler();
		}

		@Override
		@Deprecated
		public BUILDER setAutoGenerated(boolean state) {
			this.autoGenerated = state;
			return this.instance;
		}

		@SuppressWarnings("unchecked")
		public BUILDER where(Class<?> type) {
			this.type = type;
			for (IPattern p : this.archive.getPatterns(this.type)) {
				this.proxyFilter.addLabels(p.getLabels());
				if (p instanceof IBaseQueryPattern<?>)
					this.storePattern((PATTERN) p);
			}

			return this.instance;
		}

		@Override
		public BUILDER whereParam(String label, Object value) {
			this.addParam(label, value);
			return this.instance;
		}

		@Override
		public BUILDER whereId(Serializable id) {
			this.id = id;
			return this.instance;
		}

		@Override
		public BUILDER storePattern(PATTERN pattern) {
			if (pattern == null)
				this.handler.remove(IPattern.IPatternContainer.class);
			else
				this.handler.put(IPattern.IPatternContainer.class, new PatternContainerHandler(pattern));
			return this.instance;
		}

		@Override
		public BUILDER storeData(Object data) {
			AQueryBuilder.ensure(this.handler, IDataContainer.class, new DataContainerHandler())
					.setData(data);
			return this.instance;
		}

		public BUILDER setLazy(boolean lazy) {
			this.lazy = lazy;
			return this.instance;
		}

		@Override
		public BUILDER setOptional(boolean optional) {
			if (optional)
				this.handler.put(IOptional.class, new OptionalHandler(optional));
			else
				this.handler.remove(IOptional.class);
			return this.instance;
		}

		@Override
		public BUILDER setReturned(boolean returned) {
			if (returned)
				this.handler.put(IReturned.class, new ReturnedHandler(returned));
			else
				this.handler.remove(IReturned.class);
			return this.instance;
		}

		@Override
		public BUILDER setPersist(boolean persist) {
			AQueryBuilder.ensure(this.handler, IDataContainer.class, new DataContainerHandler())
					.setPersist(persist);
			return this.instance;
		}

		@Override
		public BUILDER setReadonly(boolean readonly) {
			AQueryBuilder.ensure(this.handler, IDataContainer.class, new DataContainerHandler())
					.setReadonly(readonly);
			return this.instance;
		}

		@Override
		public boolean isAutoGenerated() {
			return this.autoGenerated;
		}

		public Set<String> getLabels() {
			return this.proxyFilter.getLabels();
		}

		@SuppressWarnings("unchecked")
		@Override
		public PATTERN getStoredPattern() {
			PatternContainerHandler container = (PatternContainerHandler) handler.get(IPattern.IPatternContainer.class);
			if (container == null)
				return null;
			return (PATTERN) container.getPattern();
		}

		@Override
		public Object getStoredData() {
			DataContainerHandler container = (DataContainerHandler) handler.get(IDataContainer.class);
			if (container == null)
				return false;
			return container.getData();
		}

		@Override
		public boolean isOptional() {
			OptionalHandler container = (OptionalHandler) handler.get(IOptional.class);
			if (container == null)
				return false;
			return container.isOptional();
		}

		@Override
		public boolean isReturned() {
			ReturnedHandler container = (ReturnedHandler) handler.get(IReturned.class);
			if (container == null)
				return false;
			return container.isReturned();
		}

		@Override
		public boolean persist() {
			IDataContainer container = (IDataContainer) handler.get(IDataContainer.class);
			if (container == null)
				return false;
			return container.persist();
		}

		@Override
		public boolean isLazy() {
			return this.lazy;
		}

		@Override
		public boolean isReadonly() {
			IDataContainer container = (IDataContainer) handler.get(IDataContainer.class);
			if (container == null)
				return false;
			return container.isReadonly();
		}

		@Override
		public BUILDER asRead() {
			this.proxyFilter.setFilterType(FilterType.MATCH);
			return this.instance;
		}

		@Override
		public BUILDER asWrite() {
			this.proxyFilter.setFilterType(FilterType.CREATE);
			return this.instance;
		}

		@Override
		public BUILDER asUpdate() {
			this.proxyFilter.setFilterType(FilterType.UPDATE);
			return this.instance;
		}

		@Override
		public BUILDER asDelete() {
			this.proxyFilter.setFilterType(FilterType.DELETE);
			return this.instance;
		}

		public void prebuild() {
			// IIdentified.class
			IdTypeResolver resolver = this.archive.getIdTypeResolver();
			if (this.id != null)
				if (resolver.checkIdType(this.id.getClass()))
					this.handler.put(IIdentified.class, new IdentifiedHandler(this.id));
				else
					this.addParam(resolver.getIdAlias(), this.archive.getIdFieldConverter(this.type)
							.toProperty(this.id));

			// IDataContainer.class
			DataContainerHandler dataContainer = (DataContainerHandler) this.handler.get(IDataContainer.class);
			if (DataContainerHandler.required(dataContainer, this.proxyFilter.getFilterType())) {
				if (dataContainer == null)
					this.handler.put(IDataContainer.class, new DataContainerHandler());
			} else
				this.handler.remove(IDataContainer.class);
		}

		protected abstract void validateStructure();

		protected abstract RESULT build(final Map<IQueryBuilder<?, ?, ?>, IFilter> registry);

		@Override
		public RESULT getResult() {
			if (this.result == null) {
				this.validateStructure();
				this.build(new HashMap<>());
			}
			return this.result;
		}

		protected abstract void toRecord(final CompoundTree tree, final StringVariableGenerator gen,
				final Map<IQueryBuilder<?, ?, ?>, String> registry, final CompoundTree localTree);

		protected void toRecord(final CompoundTree tree, final StringVariableGenerator gen,
				final Map<IQueryBuilder<?, ?, ?>, String> registry) {
			if (registry.containsKey(this.instance))
				return;
			String key = gen.nextVal();
			registry.put(this.instance, key);

			CompoundTree localTree = new CompoundTree(key, this.instance.getClass()
					.getSimpleName());
			tree.append(localTree);
			localTree.append("GOAL", this.proxyFilter.getFilterType()
					.name())
					.append("ORIGIN", this.autoGenerated ? "AUTO" : "MANUAL")
					.append("VALIDATED", this.valid ? "YES" : "NOT YET");
			if (this.type != null)
				localTree.append("TYPE", this.type.getCanonicalName());
			if (this.id != null)
				localTree.append("ID", this.id.toString());
			localTree.append("STATUS", this.lazy ? "LAZY" : "COMPLETE");
			if (!this.proxyFilter.getLabels()
					.isEmpty())
				localTree.append("LABELS", String.join(", ", this.proxyFilter.getLabels()));
			toRecord(tree, gen, registry, localTree);
			for (Object handler : this.handler.values())
				ITraceable.toRecord(localTree, handler);
		}

		@Override
		public String toString() {
			CompoundTree tree = new CompoundTree("\nQueryBuilder Trace:");
			this.toRecord(tree, new StringVariableGenerator(), new HashMap<>());
			return tree.toString();
		}

		protected void addParam(String label, Object value) {
			AQueryBuilder.ensure(this.handler, IParameterized.class, new ParamHandler())
					.addParam(label, value);
		}

		@SuppressWarnings("unchecked")
		protected static <T> T ensure(final Map<Class<?>, Object> handler, Class<?> keyInterface, T instance) {
			if (!handler.containsKey(keyInterface))
				handler.put(keyInterface, instance);
			return (T) handler.get(keyInterface);
		}
	}
}
