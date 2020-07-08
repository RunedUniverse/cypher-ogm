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
	
	public IPattern getPattern(Class<?> clazz) throws Exception {
		if (this.nodes.containsKey(clazz))
			return this.nodes.get(clazz);
		if (this.relations.containsKey(clazz))
			return this.relations.get(clazz);
		throw new Exception("Unsupported Class<" + clazz.getName() + "> as @Relation found!");
	}

	public boolean isIdSet(Object entity) {
		try {
			return this.getPattern(entity.getClass()).isIdSet(entity);
		} catch (Exception e) {
			return false;
		}
	}

	public IFilter createIdFilter(Class<?> clazz, Serializable id) throws Exception {
		return this.getPattern(clazz).createIdFilter(id);
	}
	
	public DataFilter createFilter(Object entity) throws Exception{
		return this.getPattern(entity.getClass()).createFilter(entity);
	}
	
	public Object setId(Object entity, Serializable id) throws IllegalArgumentException, Exception {
		return this.getPattern(entity.getClass()).setId(entity, id);
	}
	
	public Object parse(Class<?> clazz, Serializable id, String data) throws Exception{
		return this.getPattern(clazz).parse(id, data);
	}
}
