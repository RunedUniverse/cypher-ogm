package net.runeduniverse.libs.rogm.test.dummies;

import java.util.Set;

import net.runeduniverse.libs.rogm.lang.Language.IDeleteMapper;
import net.runeduniverse.libs.rogm.lang.Language.ILoadMapper;
import net.runeduniverse.libs.rogm.lang.Language.ISaveMapper;
import net.runeduniverse.libs.rogm.lang.Language.Instance;
import net.runeduniverse.libs.rogm.querying.IDataContainer;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class DummyLanguageInstance implements Instance{

	@Override
	public ILoadMapper load(IFilter filter) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISaveMapper save(IDataContainer container, Set<IFilter> filter) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeleteMapper delete(IFilter filter, IFRelation relation) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
