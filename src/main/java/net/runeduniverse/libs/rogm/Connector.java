package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.Collection;

import net.runeduniverse.libs.rogm.enums.DatabaseType;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.Filter;

public final class Connector implements Session {

	private Language lang;
	private Parser parser;
	private Module module;

	protected Connector(DatabaseType type) {
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
		// TODO Auto-generated method stub
		
		
		return null;
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}

}