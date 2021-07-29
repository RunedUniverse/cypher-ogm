package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.querying.builder.IdentifiedHandler;
import net.runeduniverse.libs.rogm.querying.builder.NodeFilter;
import net.runeduniverse.libs.rogm.querying.builder.ParamHandler;
import net.runeduniverse.libs.rogm.querying.builder.RelationFilter;

public final class QueryBuilder {
	private final Archive archive;

	public QueryBuilder(Archive archive) {
		this.archive = archive;
	}

	public NodeQueryBuilder node() {
		return new NodeQueryBuilder(this.archive);
	}

	public RelationQueryBuilder relation() {
		return new RelationQueryBuilder(this.archive);
	}

	public static final class NodeQueryBuilder {
		private final Archive archive;
		private final NodeFilter proxyFilter;
		private final Map<Class<?>, Object> handler = new HashMap<>();

		private Class<?> type = null;
		private Serializable id = null;

		public NodeQueryBuilder(Archive archive) {
			this.archive = archive;
			this.proxyFilter = new NodeFilter(this.handler);
		}

		public NodeQueryBuilder where(Class<?> type) {
			this.type = type;
			for (IPattern p : this.archive.getPatterns(type))
				proxyFilter.addLabels(p.getLabels());
			return this;
		}

		public NodeQueryBuilder whereParam(String label, Object value) {
			this.addParam(label, value);
			return this;
		}

		public NodeQueryBuilder whereId(Serializable id) {
			this.id = id;
			return this;
		}

		protected void addParam(String label, Object value) {
			QueryBuilder.ensure(this.handler, new ParamHandler())
					.addParam(label, value);
		}

		public IFilter build() {
			if (this.archive.getCnf()
					.getModule()
					.checkIdType(id.getClass()))
				this.handler.put(IdentifiedHandler.class, new IdentifiedHandler(id));
			else
				this.addParam(this.archive.getCnf()
						.getModule()
						.getIdAlias(),
						this.archive.getIdFieldConverter(type)
								.toProperty(id));

			return (IFilter) Proxy.newProxyInstance(QueryBuilder.class.getClassLoader(), this.proxyFilter.gatherInterfaces(),
					this.proxyFilter);
		}
	}

	@RequiredArgsConstructor
	public static final class RelationQueryBuilder {
		private final Archive archive;
		private final RelationFilter proxyFilter;
		private final Map<Class<?>, Object> handler = new HashMap<>();

		private Class<?> type = null;
		private Serializable id = null;

		public RelationQueryBuilder(Archive archive) {
			this.archive = archive;
			this.proxyFilter = new RelationFilter(this.handler);
		}

		public RelationQueryBuilder whereParam(String label, Object value) {
			this.addParam(label, value);
			return this;
		}

		public RelationQueryBuilder whereId(Serializable id) {
			this.id = id;
			return this;
		}

		protected void addParam(String label, Object value) {
			QueryBuilder.ensure(this.handler, new ParamHandler())
					.addParam(label, value);
		}

		public IFilter build() {
			if (this.archive.getCnf()
					.getModule()
					.checkIdType(id.getClass()))
				this.handler.put(IdentifiedHandler.class, new IdentifiedHandler(id));
			else
				this.addParam(this.archive.getCnf()
						.getModule()
						.getIdAlias(),
						this.archive.getIdFieldConverter(type)
								.toProperty(id));

			return (IFilter) Proxy.newProxyInstance(QueryBuilder.class.getClassLoader(),
					this.proxyFilter.gatherInterfaces(), this.proxyFilter);
		}
	}

	protected static <T> T ensure(final Map<Class<?>, Object> handler, T instance) {
		Class<?> clazz = instance.getClass();
		if (!handler.containsKey(clazz))
			handler.put(clazz, instance);
		return instance;
	}
}
