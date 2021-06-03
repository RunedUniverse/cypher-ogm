package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;
import java.lang.reflect.Proxy;

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

		private LabeledHandler labeledHandler = null;
		private IdentifiedHandler identifiedHandler = null;
		private ParamHandler paramHandler = null;

		public NodeQueryBuilder where(Class<?> type) {
			this.type = type;
			this.labeledHandler = new LabeledHandler();
			for (IPattern p : this.archive.getPatterns(type))
				this.labeledHandler.addLabels(p.getLabels());
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
			if (this.paramHandler == null)
				this.paramHandler = new ParamHandler();
			this.paramHandler.addParam(label, value);
		}

		public IFilter build() {
			if (this.archive.getCnf()
					.getModule()
					.checkIdType(id.getClass())) {
				this.identifiedHandler = new IdentifiedHandler();
				this.identifiedHandler.setId(id);
			} else {
				this.addParam(this.archive.getCnf()
						.getModule()
						.getIdAlias(),
						this.archive.getIdFieldConverter(type)
								.toProperty(id));
			}

			NodeFilter handler = new NodeFilter(FilterType.MATCH, this.labeledHandler, this.identifiedHandler,
					this.paramHandler);
			return (IFilter) Proxy.newProxyInstance(QueryBuilder.class.getClassLoader(), handler.gatherInterfaces(),
					handler);
		}
	}
}
