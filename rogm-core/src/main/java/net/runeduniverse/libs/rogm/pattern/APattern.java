package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.PreReload;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.IQueryBuilder;
import net.runeduniverse.libs.scanner.MethodPattern;
import net.runeduniverse.libs.scanner.TypePattern;

public abstract class APattern<B extends IQueryBuilder<?, ?, ? extends IFilter>>
		extends TypePattern<FieldPattern, MethodPattern> implements IBaseQueryPattern<B>, IValidatable {

	protected final Archive archive;
	protected FieldPattern idFieldPattern;
	@Getter
	protected IConverter<?> idConverter = null;

	public APattern(Archive archive, String pkg, ClassLoader loader, Class<?> type) {
		super(pkg, loader, type);
		this.archive = archive;
	}

	public void validate() throws Exception {
		this.idFieldPattern = super.getField(Id.class);
		if (this.idFieldPattern != null)
			this.idConverter = this.idFieldPattern.getConverter();
		for (Map.Entry<?, FieldPattern> entry : this.fields.entrySet())
			IValidatable.validate(entry.getValue());
	}

	@Override
	public boolean isIdSet(Object entity) {
		return this.getId(entity) != null;
	}

	@Override
	public Serializable getId(Object entity) {
		if (this.idFieldPattern == null)
			return null;
		return (Serializable) this.idFieldPattern.getValue(entity);
	}

	@Override
	public Object setId(Object entity, Serializable id) {
		if (this.idFieldPattern != null)
			this.idFieldPattern.setValue(entity, id);
		return entity;
	}

	@Override
	public Serializable prepareEntityId(Serializable id, Serializable entityId) {
		if (this.idFieldPattern == null || entityId == null)
			return id;
		else if (entityId instanceof String)
			return this.idFieldPattern.getConverter()
					.convert((String) entityId);
		return entityId;
	}

	@Override
	public void prepareEntityId(IData data) {
		if (this.idFieldPattern != null)
			data.setEntityId(prepareEntityId(data.getId(), data.getEntityId()));
	}

	@Override
	public Object prepareEntityUpdate(final IBuffer buffer, IData data) {
		this.prepareEntityId(data);

		Object entity = buffer.getById(data.getId(), this.type);
		this.callMethod(PreReload.class, entity);
		return entity;
	}

	@RequiredArgsConstructor
	@Getter
	protected class DeleteContainer implements IDeleteContainer {
		private final IPattern pattern;
		private final Object entity;
		private final Serializable deletedId;
		private final IFRelation effectedFilter;
		private final IFilter deleteFilter;
	}
}
