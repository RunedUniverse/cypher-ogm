package net.runeduniverse.libs.rogm.pipeline.chain;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.lang.Language.ILoadMapper;
import net.runeduniverse.libs.rogm.lang.Language.IMapper;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.IRawDataRecord;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDataRecord;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface LookupLayers {

	@Chain(label = Chain.LOAD_ALL_CHAIN, layers = { 100 }) // TODO FIX layers
	@Chain(label = Chain.LOAD_ONE_CHAIN, layers = { 100 }) // TODO FIX layers
	public static IMapper buildQryMapper(Language.Instance lang, IFilter filter) throws Exception {
		return lang.load(filter);
	}

	@Chain(label = Chain.LOAD_ALL_CHAIN, layers = { 200 }) // TODO FIX layers
	@Chain(label = Chain.LOAD_ONE_CHAIN, layers = { 200 }) // TODO FIX layers
	public static IRawDataRecord queryDatabase(Module.Instance<?> db, IMapper mapper) {
		return db.queryObject(mapper.qry());
	}

	@Chain(label = Chain.LOAD_ALL_CHAIN, layers = { 300 }) // TODO FIX layers
	@Chain(label = Chain.LOAD_ONE_CHAIN, layers = { 300 }) // TODO FIX layers
	public static IDataRecord convertRecord(ILoadMapper mapper, IRawDataRecord rawDataRecord) {
		return mapper.parseDataRecord(rawDataRecord.getData());
	}

}
