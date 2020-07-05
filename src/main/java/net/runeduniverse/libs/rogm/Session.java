package net.runeduniverse.libs.rogm;

import java.io.Serializable;
import java.util.Collection;

import net.runeduniverse.libs.rogm.querying.IFilter;

public interface Session extends AutoCloseable {

	boolean isConnected();
	
	void save(Object object);
	void saveAll(Collection<Object> objects);
	
	<T, ID extends Serializable> T load(Class<T> type, ID id);
	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type);
	<T, ID extends Serializable> Collection<T> loadAll(Class<T> type, IFilter filter);
	
	public static Session create(Configuration cnf) {
		return new CoreSession(cnf);
	}
}
