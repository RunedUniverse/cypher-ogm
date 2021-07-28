package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.querying.builder.IdentifiedHandler;
import net.runeduniverse.libs.rogm.querying.builder.LabeledHandler;
import net.runeduniverse.libs.rogm.querying.builder.NodeFilter;
import net.runeduniverse.libs.rogm.querying.builder.ParamHandler;

public final class QueryBuilder {
	private final Archive archive;

	public QueryBuilder(Archive archive) {
		this.archive = archive;
	}

	public NodeQueryBuilder searchNode() {
		return new NodeQueryBuilder(this.archive);
	}

	@RequiredArgsConstructor
	public static final class NodeQueryBuilder {
		private final Archive archive;

		private Class<?> type = null;
		private Serializable id = null;

		private Map<Class<?>, Object> handler = new HashMap<>();

		public NodeQueryBuilder where(Class<?> type) {
			this.type = type;
			LabeledHandler labeledHandler = new LabeledHandler();
			for (IPattern p : this.archive.getPatterns(type))
				labeledHandler.addLabels(p.getLabels());
			this.handler.put(LabeledHandler.class, labeledHandler);
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

			NodeFilter handler = new NodeFilter(FilterType.MATCH, this.handler);
			return (IFilter) Proxy.newProxyInstance(QueryBuilder.class.getClassLoader(), handler.gatherInterfaces(),
					handler);
		}
	}

	protected static <T> T ensure(final Map<Class<?>, Object> handler, T instance) {
		Class<?> clazz = instance.getClass();
		if (!handler.containsKey(clazz))
			handler.put(clazz, instance);
		return instance;
	}
}
