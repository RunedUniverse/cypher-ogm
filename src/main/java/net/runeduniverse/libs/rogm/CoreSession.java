package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.IDFilterNode;
import net.runeduniverse.libs.rogm.util.Buffer;
import net.runeduniverse.libs.rogm.util.DataMap;
import net.runeduniverse.libs.rogm.util.DataMap.Value;
import net.runeduniverse.libs.rogm.util.FieldAccessor;

public final class CoreSession implements Session {

	private DatabaseType dbType;
	private Language lang;
	private Parser parser;
	private Module.Instance<?> module;
	private PatternStorage storage;

	private Buffer nodeBuffer = new Buffer();
	private Buffer relationBuffer = new Buffer();

	protected CoreSession(Configuration cnf) throws Exception {
		this.dbType = cnf.getDbType();
		this.lang = this.dbType.getLang();
		this.parser = this.dbType.getParser();
		this.module = this.dbType.getModule().build(cnf);
		this.storage = new PatternStorage(cnf.getPkgs(), this.dbType.getModule(), this.parser);

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
		try {
			// TODO move insert/update to pattern
			Language.Mapper mapper = this.lang.buildSave(this.storage.createFilter(object), this.parser);
			mapper.updateObjectIds(FIELD_ACCESSOR, this.nodeBuffer, this.module.execute(mapper.qry()));

			System.out.println(mapper.qry() + '\n');
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveAll(Collection<Object> objects) {
		objects.forEach(o -> {
			this.save(o);
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, ID extends Serializable> T load(Class<T> type, ID id) {
		T o = this.nodeBuffer.load(id, type);
		if (o != null)
			return o;

		String qry = null;
		String data = null;

		try {
			qry = this.lang.buildQuery(this.storage.createIdFilter(type, id), this.parser);
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
			return this.nodeBuffer.acquire(id, type, (T) this.storage.parse(type, id, data));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type) {
		List<String> labels = new ArrayList<>();
		getLabelsForClass(type, labels);
		return loadAll(type, new FilterNode().addLabels(labels));
	}

	private <T> void getLabelsForClass(Class<T> type, List<String> labels) {
		labels.add(type.getSimpleName());
		if (Modifier.isAbstract(type.getSuperclass().getModifiers()) || type.getSuperclass() == Object.class)
			return;
		getLabelsForClass(type.getSuperclass(), labels);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, IFilter filter) {
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
				results.add(this.nodeBuffer.acquire(id, type,
						FIELD_ACCESSOR.setObjectId(this.parser.deserialize(type, d), id)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return results;
	}

	private final FieldAccessor FIELD_ACCESSOR = new FieldAccessor() {

		public <T extends Object, ID extends Serializable> T setObjectId(T obj, ID id) {
			// no @Id field -> skip
			try {
				storage.setId(obj, id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return obj;
		}
	};

	@Getter
	@Setter
	@ToString
	public class IdentifiedUpdateFilterNode extends IDFilterNode<Serializable> implements Language.DataFilter {
		private Object data;

		public IdentifiedUpdateFilterNode(Serializable id, Object data) {
			super(id);
			this.data = data;
		}
	}

	@Getter
	@Setter
	public class ParamUpdateFilterNode extends FilterNode implements Language.DataFilter {
		private Object data;

		public ParamUpdateFilterNode(Object data) {
			this.data = data;
		}

		public ParamUpdateFilterNode(Serializable id, Object data) {
			this.data = data;
			this.addParam("_id", id);
		}
	}
}
