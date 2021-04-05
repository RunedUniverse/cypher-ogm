package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.PreReload;
import net.runeduniverse.libs.rogm.buffer.IBuffer.Entry;
import net.runeduniverse.libs.rogm.buffer.IBuffer.LoadState;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.scanner.MethodPattern;
import net.runeduniverse.libs.rogm.scanner.TypePattern;

public abstract class APattern extends TypePattern<FieldPattern, MethodPattern> implements IPattern {

	protected final IStorage factory;
	protected FieldPattern idPattern;
	@Getter
	protected IConverter<?> idConverter = null;

	public APattern(IStorage factory, String pkg, ClassLoader loader, Class<?> type) {
		super(pkg, loader, type);
		this.factory = factory;
	}

	public void validate() throws Exception{
		this.idPattern = super.getField(Id.class);
	}

	@Override
	public boolean isIdSet(Object entity) {
		return this.getId(entity) != null;
	}

	@Override
	public Serializable getId(Object entity) {
		return (Serializable) this.idPattern.getValue(entity);
	}

	@Override
	public Object setId(Object entity, Serializable id) {
		FieldPattern fp = this.getField(Id.class);
		if (fp != null)
			fp.setValue(entity, id);
		return entity;
	}

	@Override
	public Serializable prepareEntityId(Serializable id, Serializable entityId) {
		if (entityId == null)
			return id;
		else if (entityId instanceof String)
			return this.getField(Id.class)
					.getConverter()
					.convert((String) entityId);
		return entityId;
	}

	@Override
	public Object parse(IData data, LoadState loadState, Set<Entry> lazyEntries) throws Exception {
		if (this.idPattern != null)
			data.setEntityId(prepareEntityId(data.getId(), data.getEntityId()));

		return this.factory.getBuffer()
				.acquire(this, data, this.type, loadState, lazyEntries);
	}

	@Override
	public Entry update(IData data) throws Exception {
		if (this.getField(Id.class) != null)
			data.setEntityId(prepareEntityId(data.getId(), data.getEntityId()));

		Object entity = this.factory.getBuffer()
				.getById(data.getId(), this.type);

		this.callMethod(PreReload.class, entity);
		return this.factory.getBuffer()
				.update(entity, data);
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
