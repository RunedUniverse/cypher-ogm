package net.runeduniverse.libs.rogm.pattern;

import static net.runeduniverse.libs.utils.StringUtils.isBlank;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.PreSave;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.InternalBufferTypes;
import net.runeduniverse.libs.rogm.pipeline.chain.data.SaveContainer;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.NodeQueryBuilder;
import net.runeduniverse.libs.rogm.querying.QueryBuilder.RelationQueryBuilder;

public class NodePattern extends APattern implements INodePattern, InternalBufferTypes {

	@Getter
	private Set<String> labels = new HashSet<>();
	private Set<RelatedFieldPattern> relFields = new HashSet<>();

	public NodePattern(Archive archive, String pkg, ClassLoader loader, Class<?> type) {
		super(archive, pkg, loader, type);

		NodeEntity typeAnno = type.getAnnotation(NodeEntity.class);
		String label = null;
		if (typeAnno != null)
			label = typeAnno.label();
		if (isBlank(label) && !Modifier.isAbstract(type.getModifiers()))
			label = type.getSimpleName();
		if (!isBlank(label))
			this.labels.add(label);
	}

	public PatternType getPatternType() {
		return PatternType.NODE;
	}

	public NodeQueryBuilder search(boolean lazy) throws Exception {
		return this._search(this.archive.getQueryBuilder()
				.node()
				.where(this.type), lazy, false);
	}

	public NodeQueryBuilder search(Serializable id, boolean lazy) throws Exception {
		return this._search(this.archive.getQueryBuilder()
				.node()
				.where(this.type)
				.whereId(id), lazy, false);
	}

	@SuppressWarnings("deprecation")
	public NodeQueryBuilder search(RelationQueryBuilder caller, boolean lazy) {
		// includes ONLY the caller-relation filter
		NodeQueryBuilder nodeBuilder = this.archive.getQueryBuilder()
				.node()
				.where(this.type)
				.addRelation(caller);
		try {
			return this._search(nodeBuilder, lazy, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodeBuilder;
	}

	@SuppressWarnings("deprecation")
	private NodeQueryBuilder _search(NodeQueryBuilder nodeBuilder, boolean lazy, boolean optional) throws Exception {
		nodeBuilder.storePattern(this)
				.setReturned(true)
				.setOptional(optional);
		if (lazy)
			nodeBuilder.setLazy(true);
		else
			for (RelatedFieldPattern field : this.relFields)
				nodeBuilder.addRelation(field.queryRelation(nodeBuilder));
		return nodeBuilder;
	}

	@Override
	public SaveContainer save(Object entity, Integer depth) throws Exception {
		return new SaveContainer(includedData -> (IDataContainer) this.save(entity, includedData, depth)
				.getResult(), NodePattern::calcEffectedFilters);
	}

	/***
	 * implementation of FunctionalInterface
	 * <code>SaveContainer.EffectedFilterCalculator</code>
	 * 
	 * @param archive
	 * @param buffer
	 * @param includedData
	 * @return Set<IFilter> of IFilter Objects describing the effected Entities of
	 *         the save procedure
	 * @throws Exception
	 */
	private static final Set<IFilter> calcEffectedFilters(final Archive archive, final IBuffer buffer,
			final Map<Object, IQueryBuilder<?, ? extends IFilter>> includedData) throws Exception {
		Set<IFilter> set = new HashSet<>();
		for (Object object : includedData.keySet()) {
			if (!includedData.get(object)
					.persist())
				continue;
			Entry entry = buffer.getEntry(object);
			if (entry == null || entry.getLoadState() == LoadState.LAZY)
				continue;
			set.add(archive.search(entry.getType(), entry.getId(), false)
					.getResult());
		}
		return set;
	}

	public NodeQueryBuilder save(Object entity, Map<Object, IQueryBuilder<?, ? extends IFilter>> includedData,
			Integer depth) throws Exception {
		if (entity == null)
			return null;

		boolean readonly = depth == -1;
		boolean persist = 0 < depth;
		IQueryBuilder<?, ? extends IFilter> container = includedData.get(entity);
		NodeQueryBuilder nodeBuilder = null;

		if (container != null) {
			if (!(!readonly && container.isReadonly()))
				return (NodeQueryBuilder) container;
			else
				nodeBuilder = (NodeQueryBuilder) container;
		} else {
			nodeBuilder = this.archive.getQueryBuilder()
					.node()
					.where(this.type)
					.storeData(entity)
					.setPersist(persist);

			if (this.isIdSet(entity)) {
				// update (id)
				nodeBuilder.whereId(this.getId(entity))
						.asUpdate();
			} else {
				// create (!id)
				nodeBuilder.asWrite();
			}
		}

		this.callMethod(PreSave.class, entity);

		nodeBuilder.setReturned(true)
				.setReadonly(readonly);
		includedData.put(entity, nodeBuilder);

		if (persist) {
			depth = depth - 1;
			for (RelatedFieldPattern field : this.relFields)
				field.saveRelation(entity, nodeBuilder, includedData, depth);
		}

		return nodeBuilder;
	}

	@Override
	public IDeleteContainer delete(final Serializable id, Object entity) throws Exception {

		QueryBuilder qryBuilder = this.archive.getQueryBuilder();
		return new DeleteContainer(this, entity, id, qryBuilder.relation()
				.setStart(qryBuilder.node()
						.whereId(id))
				.setTarget(qryBuilder.node()
						.setReturned(true))
				.setReturned(true)
				.getResult(),
				qryBuilder.node()
						.whereId(id)
						.setReturned(true)
						.asDelete()
						.getResult());
	}

	public void setRelation(Direction direction, String label, Object entity, Object value) {
		for (RelatedFieldPattern field : this.relFields)
			if (field.getDirection()
					.equals(direction)
					&& field.getLabel()
							.equals(label)
					&& field.getType()
							.isAssignableFrom(value.getClass())) {
				field.putValue(entity, value);
				return;
			}
	}

	public void deleteRelations(Object entity) {
		for (RelatedFieldPattern field : this.relFields)
			field.clearValue(entity);
	}

	@Override
	public void deleteRelations(Object entity, Collection<Object> delEntries) {
		for (RelatedFieldPattern field : this.relFields)
			field.removeValues(entity, delEntries);
	}
}
