package net.runeduniverse.libs.rogm.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runeduniverse.libs.rogm.lang.*;
import net.runeduniverse.libs.rogm.modules.*;
import net.runeduniverse.libs.rogm.modules.neo4j.Neo4jModule;
import net.runeduniverse.libs.rogm.parser.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum DatabaseType {
	Neo4j(new Cypher(), new JSONParser(), new Neo4jModule());
	
	private Language lang;
	private Parser parser;
	private Module module;
}