package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.annotations.IConverter;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.annotations.PostDelete;
import net.runeduniverse.libs.rogm.annotations.PostLoad;
import net.runeduniverse.libs.rogm.annotations.PostSave;
import net.runeduniverse.libs.rogm.annotations.PreDelete;
import net.runeduniverse.libs.rogm.annotations.PreSave;
import net.runeduniverse.libs.rogm.buffer.IBuffer.Entry;
import net.runeduniverse.libs.rogm.buffer.IBuffer.LoadState;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

@RequiredArgsConstructor
public abstract class APattern implements IPattern {

	protected final PatternStorage storage;
	@Getter
	protected final Class<?> type;
	protected Field idField = null;
	@Getter
	protected IConverter<?> idConverter = null;

	// Events
	protected Method preSave = null;
	protected Method preDelete = null;
	protected Method postLoad = null;
	protected Method postSave = null;
	protected Method postDelete = null;

	protected boolean parseId(Field id) throws Exception {
		if (!id.isAnnotationPresent(Id.class) || this.idField != null)
			return false;

		this.idField = id;
		this.idConverter = IConverter.createConverter(id.getAnnotation(Id.class), id.getType());
		return true;
	}

	protected void parseMethods(Class<?> type) {
		for (Method method : type.getDeclaredMethods()) {
			if (method.getParameterCount() != 0)
				continue;
			method.setAccessible(true);
			if (this.preSave == null && method.isAnnotationPresent(PreSave.class)) {
				this.preSave = method;
				continue;
			}
			if (this.preDelete == null && method.isAnnotationPresent(PreDelete.class)) {
				this.preDelete = method;
				continue;
			}
			if (this.postLoad == null && method.isAnnotationPresent(PostLoad.class)) {
				this.postLoad = method;
				continue;
			}
			if (this.postSave == null && method.isAnnotationPresent(PostSave.class)) {
				this.postSave = method;
				continue;
			}
			if (this.postDelete == null && method.isAnnotationPresent(PostDelete.class))
				this.postDelete = method;
		}
	}

	@Override
	public boolean isIdSet(Object entity) throws IllegalArgumentException {
		return this.getId(entity) != null;
	}

	@Override
	public Serializable getId(Object entity) {
		if (this.idField == null || entity == null)
			return null;
		try {
			return (Serializable) this.idField.get(entity);
		} catch (IllegalAccessException e) {
		}
		return null;
	}

	@Override
	public Object setId(Object object, Serializable id) {
		if (this.idField == null)
			return object;
		try {
			this.idField.set(object, id);
		} catch (IllegalAccessException | IllegalArgumentException e) {
		}
		return object;
	}

	public Serializable prepareEntityId(Serializable id, Serializable entityId) {
		if (entityId == null)
			return id;
		else if (entityId instanceof String)
			return this.idConverter.convert((String) entityId);
		return entityId;
	}

	@Override
	public Object parse(IData data, LoadState loadState, Set<Entry> lazyEntries) throws Exception {
		if (this.idField != null)
			data.setEntityId(prepareEntityId(data.getId(), data.getEntityId()));

		return this.storage.getBuffer().acquire(this, data, this.type, loadState, lazyEntries);
	}

	@Override
	public void preSave(Object entity) {
		if (entity != null && this.preSave != null)
			try {
				this.preSave.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void preDelete(Object entity) {
		if (entity != null && this.preDelete != null)
			try {
				this.preDelete.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void postLoad(Object entity) {
		if (entity != null && this.postLoad != null)
			try {
				this.postLoad.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void postSave(Object entity) {
		if (entity != null && this.postSave != null)
			try {
				this.postSave.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void postDelete(Object entity) {
		if (entity != null && this.postDelete != null)
			try {
				this.postDelete.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
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
