package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;
import net.runeduniverse.libs.rogm.pattern.IPattern.ISaveContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

public final class CoreSession implements Session {

	private DatabaseType dbType;
	private Language.Instance lang;
	private Parser.Instance parser;
	private Module.Instance<?> module;
	private PatternStorage storage;

	protected CoreSession(Configuration cnf) throws Exception {
		this.dbType = cnf.getDbType();
		this.parser = this.dbType.getParser().build(cnf);
		this.module = this.dbType.getModule().build(cnf);
		this.lang = this.dbType.getLang().build(this.parser, this.dbType.getModule());
		this.storage = new PatternStorage(cnf, this.parser);

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
			ISaveContainer container = this.storage.createFilter(object);
			Language.IMapper mapper = this.lang.buildSave(container.getDataFilter());
			mapper.updateObjectIds(this.storage, this.module.execute(mapper.qry()));
			container.postSave();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveAll(Collection<Object> objects) {
		for (Object o : objects)
			this.save(o);
	}

	@Override
	public <T, ID extends Serializable> T load(Class<T> type, ID id) {
		T o = this.storage.getNodeBuffer().load(id, type);
		if (o != null)
			return o;

		try {
			Collection<T> all = this.loadAll(type, this.storage.createIdFilter(type, id));
			if (all.isEmpty())
				return null;
			else
				for (T t : all)
					return t;

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

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, IFilter filter) {
		try {
			Language.IMapper m = lang.buildQuery(filter);
			IPattern.IDataRecord record = m.parseData(this.module.queryObject(m.qry()));

			return this.storage.parse(type, record);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}
}
