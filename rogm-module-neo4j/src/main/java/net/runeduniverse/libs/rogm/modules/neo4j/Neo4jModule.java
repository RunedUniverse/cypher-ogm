package net.runeduniverse.libs.rogm.modules.neo4j;

import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.info.ConnectionInfo;
import net.runeduniverse.libs.rogm.modules.AModule;
import net.runeduniverse.libs.rogm.parser.Parser;

public class Neo4jModule extends AModule {

	private static final String ID_ALIAS = "_id";

	@Override
	public Instance<Long> build(final Logger logger, final Parser.Instance parser) {
		return new Neo4jModuleInstance(parser, logger);
	}

	public static String buildUri(ConnectionInfo info) {
		return info.getProtocol() + "://" + info.getUri() + ':' + info.getPort();
	}

	@Override
	public Class<?> idType() {
		return Long.class;
	}

	@Override
	public boolean checkIdType(Class<?> type) {
		if (type == null)
			return false;
		return Number.class.isAssignableFrom(type);
	}

	public String getIdAlias() {
		return ID_ALIAS;
	}
}
