package net.runeduniverse.libs.rogm.lang.cypher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.buffer.BufferTypes;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.Data;
import net.runeduniverse.libs.rogm.pattern.IPattern;
import net.runeduniverse.libs.rogm.pipeline.chain.data.UpdatedEntryContainer;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.utils.DataMap;

public class Mapper implements Language.ILoadMapper, Language.ISaveMapper, Language.IDeleteMapper, BufferTypes {

	private IFilter primary;
	private String qry;
	private String effectedQry;
	@Getter
	private Collection<IFilter> effectedQrys;
	private DataMap<IFilter, String, FilterStatus> map;
	// working data
	@Getter
	private Collection<String> persistedIds = new HashSet<>();

	protected Mapper(IFilter primaryFilter, String qry, DataMap<IFilter, String, FilterStatus> map) {
		this.primary = primaryFilter;
		this.qry = qry;
		this.map = map;
	}

	protected Mapper(CypherInstance cypher, IFilter primaryFilter, String qry, Collection<IFilter> effectedQrys,
			DataMap<IFilter, String, FilterStatus> map) {
		this.primary = primaryFilter;
		this.qry = qry;
		this.effectedQrys = effectedQrys;
		this.map = map;
	}

	protected Mapper(String qry, String effectedQry, DataMap<IFilter, String, FilterStatus> map) {
		this.qry = qry;
		this.effectedQry = effectedQry;
		this.map = map;
	}

	@Override
	public String qry() {
		return this.qry;
	}

	@Override
	public String effectedQry() {
		return this.effectedQry;
	}

	@Override
	public <ID extends Serializable> Collection<UpdatedEntryContainer> updateObjectIds(Map<String, ID> ids,
			LoadState loadState) {
		Set<UpdatedEntryContainer> col = new HashSet<>();
		this.map.forEach((filter, code) -> {
			if (filter instanceof IFRelation) {
				Object s = ids.get("id_" + code);
				if (s != null)
					this.persistedIds.add(s.toString());
			}
			if (filter instanceof IDataContainer) {
				Object data = ((IDataContainer) filter).getData();
				if (data == null)
					return;
				LoadState fLoadState = loadState;
				if (filter instanceof IFRelation)
					fLoadState = LoadState.COMPLETE;
				col.add(new UpdatedEntryContainer(ids.get("id_" + code), ids.get("eid_" + code), data, fLoadState));
			}
		});
		return col;
	}

	@Override
	public IPattern.IDataRecord parseDataRecord(List<Map<String, Data>> records) {
		/*
		 * List => 1 Map per Record-line Map => key = a - value = all data from a
		 */
		Set<Serializable> ids = new HashSet<>();
		List<Set<IPattern.IData>> recordData = new ArrayList<>();

		for (Map<String, Data> record : records) {
			ids.add(record.get(this.map.get(this.primary))
					.getId());

			Set<IPattern.IData> set = new HashSet<IPattern.IData>();
			recordData.add(set);

			for (IFilter filter : this.map.keySet()) {
				Module.Data data = record.get(this.map.get(filter));
				if (data == null || data.getId() == null)
					continue;
				set.add(new PData(data, filter));
			}
		}

		return new IPattern.IDataRecord() {
			public IFilter getPrimaryFilter() {
				return primary;
			}

			@Override
			public Set<Serializable> getIds() {
				return ids;
			}

			@Override
			public List<Set<IPattern.IData>> getData() {
				return recordData;
			}
		};
	}

	@Override
	public void updateBuffer(IBuffer buffer, Serializable deletedId, List<Map<String, Object>> effectedIds) {
		for (Map<String, Object> ids : effectedIds)
			this.map.forEach((f, c) -> {
				if (!(f instanceof IFRelation))
					return;
				IFRelation rel = (IFRelation) f;
				String code = "id_" + this.map.get(rel.getStart());
				if (!ids.containsKey(code))
					code = "id_" + this.map.get(rel.getTarget());
				buffer.eraseRelations(deletedId, (Serializable) ids.get("id_" + this.map.get(f)),
						(Serializable) ids.get(code));
			});
	}

	@Override
	public String toString() {
		return this.qry;
	}

	@Getter
	protected static class PData implements IPattern.IData {
		private Serializable id;
		@Setter
		private Serializable entityId;
		private Set<String> labels;
		private String data;
		private IFilter filter;

		protected PData(Module.Data data, IFilter filter) {
			this.id = data.getId();
			this.entityId = data.getEntityId();
			this.labels = data.getLabels();
			this.data = data.getData();
			this.filter = filter;
		}

		@Override
		public String toString() {
			return this.valuesToString();
		}
	}
}