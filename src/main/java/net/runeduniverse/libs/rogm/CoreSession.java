package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.IBuffer.Entry;
import net.runeduniverse.libs.rogm.buffer.IBuffer.LoadState;
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
	private final IBuffer buffer;

	protected CoreSession(Configuration cnf) throws Exception {
		this.dbType = cnf.getDbType();
		this.parser = this.dbType.getParser().build(cnf);
		this.module = this.dbType.getModule().build(cnf);
		this.lang = this.dbType.getLang().build(this.parser, this.dbType.getModule());
		this.storage = new PatternStorage(cnf, this.parser);
		this.buffer = this.storage.getBuffer();

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

	private <T, ID extends Serializable> T _load(Class<T> type, ID id, Integer depth) {
		T o;
		if (depth == 0)
			o = this.buffer.getByEntityId(id, type);
		else
			o = this.buffer.getCompleteByEntityId(id, type);
		if (o != null)
			return o;

		try {
			Collection<T> all = this._loadAll(type, id, depth);
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

	private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, ID id, Integer depth) {
		try {
			return this._loadAll(type, this.storage.search(type, id, depth == 0), depth);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}

	private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, Integer depth) {
		try {
			return this._loadAll(type, this.storage.search(type, depth == 0), depth);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}

	private <T> Collection<T> _loadAll(Class<T> type, IFilter filter, Integer depth) {
		if (depth < 2)
			return _loadAllObjects(type, filter, null);

		Set<Entry> stage = new HashSet<>();
		Collection<T> coll = _loadAllObjects(type, filter, stage);

		this._resolveAllLazyLoaded(stage, depth - 1);
		return coll;
	}

	private void _resolveAllLazyLoaded(Set<Entry> stage, Integer depth) {
		for (int i = 0; i < depth; i++) {
			if (stage.isEmpty())
				return;
			this._resolveAllLazyLoaded(stage);
		}
	}

	private void _resolveAllLazyLoaded(Set<Entry> stage) {
		Set<Entry> next = new HashSet<>();
		for (Entry entry : stage)
			try {
				_loadAllObjects(entry.getType(), this.storage.search(entry.getType(), entry.getId(), false), next);
			} catch (Exception e) {
				e.printStackTrace();
			}
		stage.clear();
		stage.addAll(next);
	}

	private <T> Collection<T> _loadAllObjects(Class<T> type, IFilter filter, Set<Entry> lazyEntities) {
		try {
			Language.ILoadMapper m = lang.load(filter);
			IPattern.IDataRecord record = m.parseDataRecord(this.module.queryObject(m.qry()));

			return this.storage.parse(type, record, lazyEntities);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}

	private void _save(Object entity, Integer depth) {
		if (entity == null)
			return;
		try {
			ISaveContainer container = this.storage.save(entity, depth);
			Language.ISaveMapper mapper = this.lang.save(container.getDataContainer(), container.getRelatedFilter());
			mapper.updateObjectIds(this.buffer, this.module.execute(mapper.qry()), LoadState.get(depth == 0));
			if (0 < depth) {
				Collection<String> ids = mapper.reduceIds(this.buffer, this.module);
				if (!ids.isEmpty())
					this.module.execute(this.lang.deleteRelations(ids));
			}
			container.postSave();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public <T, ID extends Serializable> T load(Class<T> type, ID id) {
		return this._load(type, id, 1);
	}

	@Override
	public <T, ID extends Serializable> T load(Class<T> type, ID id, Integer depth) {
		if (depth < 0)
			depth = 0;
		return this._load(type, id, depth);
	}

	@Override
	public <T, ID extends Serializable> T loadLazy(Class<T> type, ID id) {
		return this._load(type, id, 0);
	}

	@Override
	public <T, ID extends Serializable> T load(Class<T> type, IFilter filter) {
		try {
			Collection<T> all = this._loadAllObjects(type, filter, null);
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
		return this._loadAll(type, id, 1);
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, ID id, Integer depth) {
		if (depth < 0)
			depth = 0;
		return this._loadAll(type, id, depth);
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAllLazy(Class<T> type, ID id) {
		return this._loadAll(type, id, 0);
	}

	@Override
	public <T> Collection<T> loadAll(Class<T> type) {
		return this._loadAll(type, 1);
	}

	@Override
	public <T> Collection<T> loadAll(Class<T> type, Integer depth) {
		if (depth < 0)
			depth = 0;
		return this._loadAll(type, depth);
	}

	@Override
	public <T> Collection<T> loadAllLazy(Class<T> type) {
		return this._loadAll(type, 0);
	}

	@Override
	public <T> Collection<T> loadAll(Class<T> type, IFilter filter) {
		return this._loadAllObjects(type, filter, null);
	}

	@Override
	public void resolveLazyLoaded(Object entity) {
		this.resolveAllLazyLoaded(Arrays.asList(entity), 1);
	}

	@Override
	public void resolveLazyLoaded(Object entity, Integer depth) {
		this.resolveAllLazyLoaded(Arrays.asList(entity), depth);
	}

	@Override
	public void resolveAllLazyLoaded(Collection<? extends Object> entities) {
		this.resolveAllLazyLoaded(entities, 1);
	}

	@Override
	public void resolveAllLazyLoaded(Collection<? extends Object> entities, Integer depth) {
		Set<Entry> stage = new HashSet<>();
		for (Object entity : entities) {
			Entry entry = this.buffer.getEntry(entity);
			if (entry == null || entry.getLoadState() == LoadState.COMPLETE)
				continue;
			stage.add(entry);
		}

		this._resolveAllLazyLoaded(stage, depth);
	}

	@Override
	public void save(Object entity) {
		this._save(entity, 1);
	}

	@Override
	public void save(Object entity, Integer depth) {
		if (depth < 0)
			depth = 0;
		this._save(entity, depth);
	}

	@Override
	public void saveLazy(Object entity) {
		this._save(entity, 0);
	}

	@Override
	public void saveAll(Collection<? extends Object> entities) {
		for (Object e : entities)
			this._save(e, 1);
	}

	@Override
	public void saveAll(Collection<? extends Object> entities, Integer depth) {
		if (depth < 0)
			depth = 0;
		for (Object e : entities)
			this._save(e, depth);
	}

	@Override
	public void saveAllLazy(Collection<? extends Object> entities) {
		for (Object e : entities)
			this._save(e, 0);
	}

	@Override
	public void delete(Object entity) {
		try {
			IPattern.IDeleteContainer container = this.storage.delete(entity);
			Language.IDeleteMapper mapper = this.lang.delete(container.getDeleteFilter(),
					container.getEffectedFilter());
			mapper.updateBuffer(this.buffer, container.getDeletedId(), this.module.query(mapper.effectedQry()));
			this.module.execute(mapper.qry());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteAll(Collection<? extends Object> entities) {
		for (Object e : entities)
			this.delete(e);
	}

	@Override
	public void unload(Object entity) {
		this.buffer.removeEntry(entity);
	}

	@Override
	public void unloadAll(Collection<? extends Object> entities) {
		for (Object object : entities)
			this.unload(object);
	}

	@Override
	public IPattern getPattern(Class<?> type) throws Exception {
		return this.storage.getPattern(type);
	}
}
