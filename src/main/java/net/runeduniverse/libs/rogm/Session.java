package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.Collection;

public interface Session {

	void save(Object object);
	void saveAll(Collection<Object> objects);
	
	<T, ID extends Serializable> T load(Class<T> type, ID id);
	<T, ID extends Serializable> T loadAll(Class<T> type);
	
	public static Session create(Configuration cnf) {
		return new Connector(cnf.getDbType());
	}
}
