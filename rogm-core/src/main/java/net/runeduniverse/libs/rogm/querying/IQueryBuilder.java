package net.runeduniverse.libs.rogm.querying;

import java.io.Serializable;
import java.util.Set;

import net.runeduniverse.libs.rogm.pattern.IPattern;

public interface IQueryBuilder<B extends IQueryBuilder<?, R>, R extends IFilter> {

	public B where(Class<?> type);

	public B whereParam(String label, Object value);

	public B whereId(Serializable id);

	public B storePattern(IPattern pattern);

	public B storeData(Object data);

	public B setOptional(boolean optional);

	public B setReturned(boolean returned);

	public B setPersist(boolean returned);

	public B setReadonly(boolean returned);

	public Set<String> getLabels();

	public boolean persist();

	public boolean isReadonly();

	public B asRead();

	public B asWrite();

	public B asUpdate();

	public B asDelete();

	public R build();

	public R getResult();
}
