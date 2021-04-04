package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.PreReload;
import net.runeduniverse.libs.rogm.buffer.IBuffer.Entry;
import net.runeduniverse.libs.rogm.buffer.IBuffer.LoadState;
import net.runeduniverse.libs.rogm.scanner.MethodPattern;
import net.runeduniverse.libs.rogm.scanner.TypePattern;

public abstract class APattern extends TypePattern<FieldPattern, MethodPattern> implements IPattern {

	protected final IStorage factory;

	@Getter
	@Setter
	protected IConverter<?> idConverter = null;

	public APattern(IStorage factory, String pkg, ClassLoader loader, Class<?> type) {
		super(pkg, loader, type);
		this.factory = factory;
	}

	@Override
	public boolean isIdSet(Object entity) {
		return this.getId(entity) != null;
	}

	@Override
	public Serializable getId(Object entity) {
		FieldPattern fp = this.getField(Id.class);
		if (fp == null)
			return null;
		return (Serializable) fp.getValue(entity);
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
			return this.idConverter.convert((String) entityId);
		return entityId;
	}

	@Override
	public Object parse(IData data, LoadState loadState, Set<Entry> lazyEntries) throws Exception {
		if (this.getField(Id.class) != null)
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

}
