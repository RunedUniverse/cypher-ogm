package net.runeduniverse.libs.rogm.pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class PatternStorage {

	private Map<Class<?>, IPattern> patterns = new HashMap<>();

	public PatternStorage(List<String> pkts) {

		Reflections reflections = new Reflections(pkts.toArray(), new TypeAnnotationsScanner(),
				new SubTypesScanner(true));

		reflections.getTypesAnnotatedWith(NodeEntity.class).forEach(c -> {
			this.patterns.put(c, _parse(c, EntityType.NODE));
		});
		reflections.getTypesAnnotatedWith(RelationshipEntity.class).forEach(c -> {
			this.patterns.put(c, _parse(c, EntityType.RELATION));
		});
	}

	public EntityType getEntityType(Class<?> clazz) {
		if (!this.patterns.containsKey(clazz))
			return EntityType.UNKNOWN;
		return this.patterns.get(clazz).getEntityType();
	}

	public IFilter createFilter(Class<?> type, int depth) {
		if (!this.patterns.containsKey(type))
			return null;
		return this.patterns.get(type).createFilter(depth);
	}

	private IPattern _parse(Class<?> type, EntityType entityType) {
		switch (entityType) {
		case NODE:
			return new NodePattern(type);
		case RELATION:
			return new RelationPattern(type);
		default:
			return null;
		}
	}
}
