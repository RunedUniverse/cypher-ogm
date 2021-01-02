package net.runeduniverse.libs.rogm.modules.neo4j;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.lang.cypher.CypherLanguage;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.parser.json.Feature;
import net.runeduniverse.libs.rogm.parser.json.JSONParser;

public class Neo4jConfiguration extends Configuration {

	private static final Parser PARSER = new JSONParser().configure(Feature.SERIALIZER_QUOTE_FIELD_NAMES, false)
			.configure(Feature.DESERIALIZER_ALLOW_UNQUOTED_FIELD_NAMES, true)
			.configure(Feature.MAPPER_AUTO_DETECT_GETTERS, false)
			.configure(Feature.MAPPER_AUTO_DETECT_IS_GETTERS, false);
	private static final Language LANGUAGE = new CypherLanguage();
	private static final Module MODULE = new Neo4jModule();

	public Neo4jConfiguration(String uri) {
		super(Neo4jConfiguration.PARSER, Neo4jConfiguration.LANGUAGE, Neo4jConfiguration.MODULE, uri);
		this.protocol = "bolt";
		this.port = 7687;
	}

}
