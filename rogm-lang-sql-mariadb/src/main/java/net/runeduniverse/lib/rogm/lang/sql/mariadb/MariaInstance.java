package net.runeduniverse.lib.rogm.lang.sql.mariadb;

import java.util.Set;

import net.runeduniverse.lib.rogm.lang.Language.IDeleteMapper;
import net.runeduniverse.lib.rogm.lang.Language.ILoadMapper;
import net.runeduniverse.lib.rogm.lang.Language.ISaveMapper;
import net.runeduniverse.lib.rogm.lang.Language.Instance;
import net.runeduniverse.lib.rogm.modules.IdTypeResolver;
import net.runeduniverse.lib.rogm.parser.Parser;
import net.runeduniverse.lib.rogm.querying.IDataContainer;
import net.runeduniverse.lib.rogm.querying.IFRelation;
import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.utils.logging.UniversalLogger;

public class MariaInstance implements Instance {

	public MariaInstance(IdTypeResolver resolver, Parser.Instance parser,
			UniversalLogger universalLogger) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ILoadMapper load(IFilter filter) throws Exception {
		_select(filter);
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
	
	
	
	private StringBuilder _select(IFilter filter) {
		StringBuilder qry=new StringBuilder();
		
		
		qry.append("");
		
		
		
		
		return null;
	}

}
