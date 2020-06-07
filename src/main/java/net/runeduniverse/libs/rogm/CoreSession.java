package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.runeduniverse.libs.rogm.enums.DatabaseType;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.Filter;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.IDFilter;

public final class CoreSession implements Session {

	private Language lang;
	private Parser parser;
	private Module module;

	protected CoreSession(DatabaseType type) {
		this.lang = type.getLang();
		this.parser = type.getParser();
		this.module = type.getModule();
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

		try {
			qry = lang.buildQuery(new IDFilter<ID>(id));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// TODO request data
		
		// TODO parse data
		
		return null;
	}

	@Override
	public <T> Collection<T> loadAll(Class<T> type) {
		String qry = null;

		List<String> labels = new ArrayList<>();
		
		// TODO: retrieve all labels from type
		labels.add(type.getSimpleName());
		
		
		try {
			qry = lang.buildQuery(new FilterNode().addLabels(labels));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// TODO request data
		
		// TODO parse data

		return null;
	}

	@Override
	public <T> Collection<T> loadAll(Class<T> type, Filter filter) {
		String qry = null;

		try {
			qry = lang.buildQuery(filter);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// TODO request data
		
		// TODO parse data
		
		return null;
	}

}
