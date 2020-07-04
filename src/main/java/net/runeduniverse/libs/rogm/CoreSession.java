package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.annotations.Id;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.lang.Language.DataFilter;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.Filter;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.IDFilter;
import net.runeduniverse.libs.rogm.util.DataMap;
import net.runeduniverse.libs.rogm.util.DataMap.Value;
import net.runeduniverse.libs.rogm.util.FieldAccessor;

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
		// TODO: save
		Field idField = FIELD_ACCESSOR.findAnnotatedField(object.getClass(), Id.class);
		try {
			if (idField == null || idField.get(object)==null) {
				this._create(object);
			} else
				this._update(idField, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void _create(Object object) throws Exception{
		
		ParamUpdateFilter createData = new ParamUpdateFilter(object);
		
		// TODO retrieve all labels from type
		createData.addLabel(object.getClass().getSimpleName());
		
		Language.Mapper mapper = this.lang.buildInsert(createData, this.parser);
		System.out.println(mapper.qry());
		mapper.updateObjectIds(FIELD_ACCESSOR, this.module.execute(mapper.qry()));
		System.out.println("CREATED");
		
	}

	private void _update(Field idField, Object object) throws Exception {
		DataFilter df = null;
		// class java.lang.Long
		if (Serializable.class.isAssignableFrom(idField.getType())) {
			// IdentifiedFilter
			df = new IdentifiedUpdateFilter((Serializable) idField.get(object), object);
		} else {
			// ParamFilter
			df = new ParamUpdateFilter(object);
		}
		
		Language.Mapper mapper = this.lang.buildUpdate(df, this.parser);
		System.out.println("UPDATE:\n" + mapper.qry());
		// TODO: debug & finish
		
		mapper.updateObjectIds(FIELD_ACCESSOR, this.module.execute(mapper.qry()));

	}

	@Override
	public void saveAll(Collection<Object> objects) {
		objects.forEach(o -> {
			this.save(o);
		});
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

		for (Value<String, ?> e : this.module.queryObject(qry).valueSet()) {
			// get only first entry
			data = e.getValue();
			break;
		}

		try {
			return FIELD_ACCESSOR.setObjectId(this.parser.deserialize(type, data), id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type) {
		List<String> labels = new ArrayList<>();
		// TODO retrieve all labels from type
		// Stop when
		/*
		 * Super is Abstract
		 * ... is Object
		 * ... is Interface
		 * */
		helperMethodForMethodAbove(type, labels);
		labels.add(type.getSimpleName());
		return loadAll(type, new FilterNode().addLabels(labels));
	}
	
	private <T> void helperMethodForMethodAbove(Class<T> type, List<String> labels) {
		labels.add(type.getSimpleName());
		if(Modifier.isAbstract(type.getSuperclass().getModifiers())||type.getSuperclass()==Object.class) {
			return;
		}else {
			helperMethodForMethodAbove(type.getSuperclass(), labels);
		}
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

		DataMap<ID, String, String> data = (DataMap<ID, String, String>) this.module.queryObject(qry);

		data.forEach((id, d, code) -> {
			try {
				results.add(FIELD_ACCESSOR.setObjectId(this.parser.deserialize(type, d), id));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		/*
		 * for (Entry<ID, String> entry : data.entrySet()) try {
		 * results.add(_merge(this.parser.deserialize(type, entry.getValue()),
		 * entry.getKey())); } catch (Exception e) { e.printStackTrace(); }
		 */

		return results;
	}

	private final static FieldAccessor FIELD_ACCESSOR = new FieldAccessor() {
	
		public <T extends Object, ID extends Serializable> T setObjectId(T obj, ID id) {
			// no @Id field -> skip
			Field field = findAnnotatedField(obj.getClass(), Id.class);
			if (field != null)
				try {
					field.set(obj, field.getType().cast(id));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			return obj;
		}
		
		public <ANNO extends Annotation> Field findAnnotatedField(Class<?> clazz, Class<ANNO> anno) {
			if (clazz.isAssignableFrom(Object.class))
				return null;
			for (Field field : clazz.getDeclaredFields())
				if (field.isAnnotationPresent(anno)) {
					field.setAccessible(true);
					return field;
				}
			return findAnnotatedField(clazz.getSuperclass(), anno);
		}
	};

	@Getter
	@Setter
	@ToString
	protected class IdentifiedUpdateFilter extends IDFilter<Serializable> implements Language.DataFilter {
		private Object data;

		public IdentifiedUpdateFilter(Serializable id, Object data) {
			super(id);
			this.data = data;
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	protected class ParamUpdateFilter extends FilterNode implements Language.DataFilter {
		private Object data;
	}
}
