package net.runeduniverse.libs.rogm.pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;

public class PatternStorage {

	@Getter
	private final FilterFactory factory;
	@Getter
	private final Parser parser;
	private final Map<Class<?>, NodePattern> nodes = new HashMap<>();
	private final Map<Class<?>, RelationPattern> relations = new HashMap<>();

	public PatternStorage(List<String> pkts, Module module, Parser parser) {
		this.factory = new FilterFactory(module);
		this.parser = parser;

		Reflections reflections = new Reflections(pkts.toArray(), new TypeAnnotationsScanner(),
				new SubTypesScanner(true));

		reflections.getTypesAnnotatedWith(NodeEntity.class).forEach(c -> {
			this.nodes.put(c, new NodePattern(this, c));
		});
		reflections.getTypesAnnotatedWith(RelationshipEntity.class).forEach(c -> {
			this.relations.put(c, new RelationPattern(this, c));
		});
	}

	public EntityType getEntityType(Class<?> clazz) {
		if (this.nodes.containsKey(clazz))
			return EntityType.NODE;
		if (this.relations.containsKey(clazz))
			return EntityType.RELATION;
		return EntityType.UNKNOWN;
	}

	public NodePattern getNode(Class<?> clazz) {
		return this.nodes.get(clazz);
	}

	public RelationPattern getRelation(Class<?> clazz) {
		return this.relations.get(clazz);
	}
}
