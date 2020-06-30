package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.Filter;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.IDFilter;

public final class CoreSession implements Session {

	private DatabaseType dbType;
	private Language lang;
	private Parser parser;
	private Module.Instance<?> module;

	protected CoreSession(Configuration cnf) {
		this.dbType = cnf.getDbType();
		this.lang = this.dbType.getLang();
		this.parser = this.dbType.getParser();
		this.module = this.dbType.getModule().build(cnf);

		this.module.connect(cnf);
	}

	@Override
	public void close() throws Exception {
		this.module.disconnect();
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	@Override
	public boolean isConnected() {
		return this.module.isConnected();
	}

	@Override
	public void save(Object object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveAll(Collection<Object> objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T, ID extends Serializable> T load(Class<T> type, ID id) {
		String qry = null;
		String data = null;

		try {
			qry = this.lang.buildQuery(new IDFilter<ID>(id), this.parser);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		for (Entry<?, String> e : this.module.queryObject(qry).entrySet()) {
			// get only first entry
			data = e.getValue();
			break;
		}

		try {
			return _merge(this.parser.deserialize(type, data), id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type) {
		List<String> labels = new ArrayList<>();

		// TODO retrieve all labels from type

		labels.add(type.getSimpleName());
		return loadAll(type, new FilterNode().addLabels(labels));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, Filter filter) {
		String qry = null;
		Collection<T> results = new ArrayList<>();

		try {
			qry = lang.buildQuery(filter, this.parser);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Map<ID, String> data = (Map<ID, String>) this.module.queryObject(qry);

		for (Entry<ID, String> entry : data.entrySet())
			try {
				results.add(_merge(this.parser.deserialize(type, entry.getValue()), entry.getKey()));
			} catch (Exception e) {
				e.printStackTrace();
			}

		return results;
	}

	private <T, ID extends Serializable> T _merge(T obj, ID id) {

		Field field = _findAnnotatedField(obj.getClass(), Id.class);
		field.setAccessible(true);
		if (field != null)
			try {
				field.set(obj, field.getType().cast(id));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		return obj;
	}

	private <ANNO extends Annotation> Field _findAnnotatedField(Class<?> clazz, Class<ANNO> anno) {
		if (clazz.isAssignableFrom(Object.class))
			return null;
		for (Field field : clazz.getDeclaredFields())
			if (field.isAnnotationPresent(anno))
				return field;
		return _findAnnotatedField(clazz.getSuperclass(), anno);
	}

}
