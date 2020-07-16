package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runeduniverse.libs.rogm.annotations.PostDelete;
import net.runeduniverse.libs.rogm.annotations.PostLoad;
import net.runeduniverse.libs.rogm.annotations.Post⁮Save;
import net.runeduniverse.libs.rogm.annotations.PreDelete;
import net.runeduniverse.libs.rogm.annotations.PreSave;

@RequiredArgsConstructor
public abstract class APattern implements IPattern {

	protected final PatternStorage storage;
	@Getter
	protected final Class<?> type;
	protected Field idField = null;

	// Events
	protected Method preSave = null;
	protected Method preDelete = null;
	protected Method postLoad = null;
	protected Method postSave = null;
	protected Method postDelete = null;

	protected void parseMethods(Class<?> type) {
		for (Method method : type.getDeclaredMethods()) {
			if (method.getParameterCount() != 0)
				continue;
			method.setAccessible(true);
			if (this.preSave == null && method.isAnnotationPresent(PreSave.class)) {
				this.preSave = method;
				continue;
			}
			if (this.preDelete == null && method.isAnnotationPresent(PreDelete.class)) { // TODO implement
				this.preDelete = method;
				continue;
			}
			if (this.postLoad == null && method.isAnnotationPresent(PostLoad.class)) {
				this.postLoad = method;
				continue;
			}
			if (this.postSave == null && method.isAnnotationPresent(Post⁮Save.class)) {
				this.postSave = method;
				continue;
			}
			if (this.postDelete == null && method.isAnnotationPresent(PostDelete.class)) // TODO implement
				this.postDelete = method;
		}
	}

	@Override
	public boolean isIdSet(Object entity) throws IllegalArgumentException {
		return this.getId(entity) != null;
	}

	@Override
	public Serializable getId(Object entity) {
		if (this.idField == null)
			return null;
		try {
			return (Serializable) this.idField.get(entity);
		} catch (IllegalAccessException e) {
		}
		return null;
	}

	@Override
	public Object setId(Object object, Serializable id) throws IllegalArgumentException {
		if (this.idField == null)
			return object;
		try {
			this.idField.set(object, id);
		} catch (IllegalAccessException e) {
		}
		return object;
	}

	@Override
	public Object parse(Serializable id, String data) throws Exception {
		return this.setId(this.storage.getParser().deserialize(this.type, data), id);
	}

	@Override
	public Object parse(IData data) throws Exception {
		Object node = this.getBuffer().load(data.getId(), this.type);
		if (node != null)
			return node;
		node = this.parse(data.getId(), data.getData());
		this.getBuffer().save(data.getId(), node);
		return node;
	}

	@Override
	public void preSave(Object entity) {
		if (this.preSave != null)
			try {
				this.preSave.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void preDelete(Object entity) {
		if (this.preDelete != null)
			try {
				this.preDelete.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void postLoad(Object entity) {
		if (this.postLoad != null)
			try {
				this.postLoad.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void postSave(Object entity) {
		if (this.postSave != null)
			try {
				this.postSave.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void postDelete(Object entity) {
		if (this.postDelete != null)
			try {
				this.postDelete.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}
}
