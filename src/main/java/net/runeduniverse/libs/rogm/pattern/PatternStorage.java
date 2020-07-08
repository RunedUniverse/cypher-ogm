package net.runeduniverse.libs.rogm.pattern;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import lombok.Getter;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.lang.Language.DataFilter;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class PatternStorage {

	@Getter
	private final FilterFactory factory;
	@Getter
	private final Parser parser;
	private final Map<Class<?>, NodePattern> nodes = new HashMap<>();
	private final Map<Class<?>, RelationPattern> relations = new HashMap<>();

	public PatternStorage(List<String> pkgs, Module module, Parser parser) throws Exception {
		this.factory = new FilterFactory(module);
		this.parser = parser;

		Reflections reflections = new Reflections(pkgs.toArray(), new TypeAnnotationsScanner(),
				new SubTypesScanner(true));

		for (Class<?> c : reflections.getTypesAnnotatedWith(NodeEntity.class))
			this.nodes.put(c, new NodePattern(this, c));
		for (Class<?> c : reflections.getTypesAnnotatedWith(RelationshipEntity.class))
			this.relations.put(c, new RelationPattern(this, c));
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

	public boolean isIdSet(Object entity) {
		if (this.nodes.containsKey(entity.getClass()))
			return this.nodes.get(entity.getClass()).isIdSet(entity);
		if (this.relations.containsKey(entity.getClass()))
			return this.relations.get(entity.getClass()).isIdSet(entity);
		return false;
	}

	public IFilter createIdFilter(Class<?> clazz, Serializable id) throws Exception {
		if (this.nodes.containsKey(clazz))
			return this.nodes.get(clazz).createIdFilter(id);
		if (this.relations.containsKey(clazz))
			return this.relations.get(clazz).createIdFilter(id);
		throw new Exception("Unsupported Class<" + clazz.getName() + "> as @Relation found!");
	}
	
	public DataFilter createFilter(Object entity) throws Exception{
		if (this.nodes.containsKey(entity.getClass()))
			return this.nodes.get(entity.getClass()).createFilter(entity);
		if (this.relations.containsKey(entity.getClass()))
			return this.relations.get(entity.getClass()).createFilter(entity);
		throw new Exception("Unsupported Class<" + entity.getClass().getName() + "> as @Relation found!");
	}
}
