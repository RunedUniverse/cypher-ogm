package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.PatternStorage;
import net.runeduniverse.libs.rogm.pattern.IPattern.ISaveContainer;
import net.runeduniverse.libs.rogm.pattern.IStorage;
import net.runeduniverse.libs.rogm.querying.IFilter;

public final class CoreSession implements Session {

	private final DatabaseType dbType;
	private final Language.Instance lang;
	private final Parser.Instance parser;
	private final Module.Instance<?> module;
	private final IStorage storage;

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
	public <T, ID extends Serializable> T load(Class<T> type, ID id) {
		return this._load(type, id, false);
	}

	@Override
	public <T, ID extends Serializable> T loadLazy(Class<T> type, ID id) {
		return this._load(type, id, true);
	}

	private <T, ID extends Serializable> T _load(Class<T> type, ID id, boolean lazy) {
		T o = this.storage.getBuffer().getByEntityId(id, type);
		if (o != null)
			return o;

		try {
			Collection<T> all = this._loadAll(type, id, lazy);
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
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id) {
		return this._loadAll(type, id, false);
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAllLazy(Class<T> type, ID id) {
		return this._loadAll(type, id, true);
	}

	private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, ID id, boolean lazy) {
		try {
			return this.loadAll(type, this.storage.search(type, id, lazy));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public <T> Collection<T> loadAll(Class<T> type) {
		return this._loadAll(type, false);
	}

	@Override
	public <T> Collection<T> loadAllLazy(Class<T> type) {
		return this._loadAll(type, true);
	}

	private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, boolean lazy) {
		try {
			return loadAll(type, this.storage.search(type, lazy));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}

	@Override
	public <T> Collection<T> loadAll(Class<T> type, IFilter filter) {
		try {
			Language.ILoadMapper m = lang.load(filter);
			IPattern.IDataRecord record = m.parseDataRecord(this.module.queryObject(m.qry()));

			return this.storage.parse(type, record);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}

	@Override
	public void save(Object entity) {
		this._save(entity, false);
	}

	@Override
	public void saveLazy(Object entity) {
		this._save(entity, false);
	}
	
	private void _save(Object entity, boolean lazy) {
		try {
			ISaveContainer container = this.storage.save(entity, lazy);
			Language.ISaveMapper mapper = this.lang.save(container.getDataContainer(), container.getRelatedFilter());
			mapper.updateObjectIds(this.storage.getBuffer(), this.module.execute(mapper.qry()));
			if (mapper.effectedQry() != null) {
				Collection<String> ids = mapper.reduceIds(this.storage.getBuffer(),
						this.module.query(mapper.effectedQry()));
				this.module.execute(this.lang.deleteRelations(ids));
			}
			container.postSave();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveAll(Collection<Object> entities) {
		for (Object e : entities)
			this.save(e);
	}

	@Override
	public void saveAllLazy(Collection<Object> entities) {
		// TODO lazy loading

	}

	@Override
	public void delete(Object entity) {
		try {
			IPattern.IDeleteContainer container = this.storage.delete(entity);
			Language.IDeleteMapper mapper = this.lang.delete(container.getDeleteFilter(),
					container.getEffectedFilter());
			mapper.updateBuffer(this.storage.getBuffer(), container.getDeletedId(),
					this.module.query(mapper.effectedQry()));
			this.module.execute(mapper.qry());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteAll(Collection<Object> entities) {
		for (Object e : entities)
			this.delete(e);
	}
}
