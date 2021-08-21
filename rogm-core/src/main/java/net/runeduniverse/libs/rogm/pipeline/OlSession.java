package net.runeduniverse.libs.rogm.pipeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern.ISaveContainer;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class OlSession {
	// TODO invoke Transaction
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
				this.logger.log(Level.WARNING, "Loading of Class<" + type.getCanonicalName() + "> Entity with id=" + id
						+ " (depth=" + depth + ") failed!", e);
			}
			return null;
		}

		private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, ID id, Integer depth) {
			try {
				return this._loadAll(type, this.storage.search(type, id, depth == 0), depth);
			} catch (Exception e) {
				this.logger.log(Level.WARNING, "Loading of Class<" + type.getCanonicalName() + "> Entities with id=" + id
						+ " (depth=" + depth + ") failed!", e);
			}
			return new ArrayList<T>();
		}

		private <T, ID extends Serializable> Collection<T> _loadAll(Class<T> type, Integer depth) {
			try {
				return this._loadAll(type, this.storage.search(type, depth == 0), depth);
			} catch (Exception e) {
				this.logger.log(Level.WARNING,
						"Loading of Class<" + type.getCanonicalName() + "> Entities" + " (depth=" + depth + ") failed!", e);
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
					this.logger.log(Level.WARNING, "Resolving of lazy loaded Buffer-Entry failed!", e);
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
				this.logger.log(Level.WARNING, "Loading of Class<" + type.getCanonicalName() + "> Entities failed!", e);
				return new ArrayList<T>();
			}
		}

		private void _reloadAllObjects(Set<Object> entities, Integer depth) {
			Set<Entry> stage = new HashSet<>();

			for (Object entity : entities)
				if (entity != null)
					try {
						this._reloadObject(entity, this.storage.search(entity, depth == 0), depth < 2 ? null : stage);
					} catch (Exception e) {
						this.logger.log(Level.WARNING, "Loading of Class<" + entity.getClass()
								.getCanonicalName() + "> Entity" + " (depth=" + depth + ") failed!", e);
					}

			for (int i = 0; i < depth - 1; i++) {
				if (stage.isEmpty())
					return;
				this._reloadRelatedObjects(stage);
			}
		}

		private void _reloadRelatedObjects(Set<Entry> stage) {
			Set<Entry> next = new HashSet<>();
			for (Entry entry : stage)
				if (entry != null)
					try {
						_reloadObject(entry.getEntity(), this.storage.search(entry.getType(), entry.getId(), false), next);
					} catch (Exception e) {
						this.logger.log(Level.WARNING, "Resolving of reloaded-related Buffer-Entry failed!", e);
					}
			stage.clear();
			stage.addAll(next);
		}

		private void _reloadObject(Object entity, IFilter filter, Set<Entry> relatedEntities) {
			try {
				Language.ILoadMapper m = lang.load(filter);
				IPattern.IDataRecord record = m.parseDataRecord(this.module.queryObject(m.qry()));

				this.storage.update(entity, record, relatedEntities);
			} catch (Exception e) {
				this.logger.log(Level.WARNING, "Reloading of Class<" + entity.getClass()
						.getCanonicalName() + "> Entity failed!", e);
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
				this.logger.log(Level.WARNING, "Saving of Class<" + entity.getClass()
						.getCanonicalName() + "> Entity failed! (depth=" + depth + ')', e);
			}
		}
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
