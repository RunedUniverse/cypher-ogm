package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.pattern.Archive;
import net.runeduniverse.libs.rogm.pattern.INodePattern;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IRelationPattern;

public final class QueryBuilder {
	private final Archive archive;

	public QueryBuilder(Archive archive) {
		this.archive = archive;
	}

	public Instance search(Class<?> type) {
		for (IPattern p : this.archive.getPatterns(type)) {
			if (p instanceof INodePattern)
				return new NodeResult(this.archive);
			if (p instanceof IRelationPattern)
				return new RelationResult(this.archive);
		}
		return new NoResult();
	}

	public static interface Instance {
		void whereId(Serializable id);
		IFilter build();
	}

	@RequiredArgsConstructor
	protected class NodeResult implements Instance {
		private final Archive archive;
		private FilterNode filter;

		@Override
		public void whereId(Serializable id) {
			
		}

		@Override
		public IFilter build() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@RequiredArgsConstructor
	protected class RelationResult implements Instance {
		private final Archive archive;
		private FilterRelation filter;

		@Override
		public void whereId(Serializable id) {
			// TODO Auto-generated method stub

		}

		@Override
		public IFilter build() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	protected class NoResult implements Instance {
		@Override
		public void whereId(Serializable id) {
		}

		@Override
		public IFilter build() {
			return null;
		}
	}
}
