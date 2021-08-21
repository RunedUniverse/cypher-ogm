package net.runeduniverse.libs.rogm.pipeline.chain;

import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.lang.Language.ILoadMapper;
import net.runeduniverse.libs.rogm.lang.Language.IMapper;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.Module.IRawDataRecord;
import net.runeduniverse.libs.rogm.pattern.IPattern.IDataRecord;
import net.runeduniverse.libs.rogm.pipeline.chain.sys.Chain;
import net.runeduniverse.libs.rogm.querying.IFilter;

public interface LookupLayers {

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = { Chains.LOAD_CHAIN.ALL.BUILD_QUERY_MAPPER })
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.BUILD_QUERY_MAPPER })
	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL, layers = { Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.BUILD_QUERY_MAPPER })
	public static IMapper buildQryMapper(Language.Instance lang, IFilter filter) throws Exception {
		return lang.load(filter);
	}

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = { Chains.LOAD_CHAIN.ALL.QUERY_DATABASE_FOR_RAW_DATA_RECORD })
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = { Chains.LOAD_CHAIN.ONE.QUERY_DATABASE_FOR_RAW_DATA_RECORD })
	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL, layers = { Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.QUERY_DATABASE_FOR_RAW_DATA_RECORD })
	public static IRawDataRecord queryDatabase(Module.Instance<?> db, IMapper mapper) {
		return db.queryObject(mapper.qry());
	}

	@Chain(label = Chains.LOAD_CHAIN.ALL.LABEL, layers = {
			Chains.LOAD_CHAIN.ALL.CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD })
	@Chain(label = Chains.LOAD_CHAIN.ONE.LABEL, layers = {
			Chains.LOAD_CHAIN.ONE.CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD })
	@Chain(label = Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.LABEL, layers = {
			Chains.LOAD_CHAIN.RESOLVE_LAZY.SELECTED.CONVERT_RAW_DATA_RECORD_TO_DATA_RECORD })
	public static IDataRecord convertRecord(ILoadMapper mapper, IRawDataRecord rawDataRecord) {
		return mapper.parseDataRecord(rawDataRecord.getData());
	}

}
