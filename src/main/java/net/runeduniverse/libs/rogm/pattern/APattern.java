package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.lang.reflect.Field;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class APattern implements IPattern {

	protected final PatternStorage storage;
	@Getter
	protected final Class<?> type;
	protected Field idField = null;
	
	@Override
	public boolean isIdSet(Object entity) throws IllegalArgumentException {
		return this.getId(entity)!=null;
	}

	@Override
	public Serializable getId(Object entity) {
		if(this.idField == null)
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

}
