package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.Data;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.IDFilterNode;

public final class CoreSession implements Session {

	private DatabaseType dbType;
	private Language lang;
	private Parser parser;
	private Module.Instance<?> module;
	private PatternStorage storage;

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

			System.out.println(mapper.qry());

			mapper.updateObjectIds(this.storage, this.module.execute(mapper.qry()));

			System.out.println(mapper.qry() + '\n');
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveAll(Collection<Object> objects) {
		for (Object o : objects)
			this.save(o);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, ID extends Serializable> T load(Class<T> type, ID id) {
		T o = this.storage.getNodeBuffer().load(id, type);
		if (o != null)
			return o;

		try {
			Language.Mapper m = this.lang.buildQuery(this.storage.createIdFilter(type, id), this.parser);
			for (Map<String, Data> d : this.module.queryObject(m.qry()))
				// get only first entry
				return (T) m.parseObject(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type) {
		try {
			return loadAll(type, this.storage.createFilter(type));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, IFilter filter) {
		try {
			Language.Mapper m = lang.buildQuery(filter, this.parser);
			return (Collection<T>) m.parseObjects(this.module.queryObject(m.qry()));
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}

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
