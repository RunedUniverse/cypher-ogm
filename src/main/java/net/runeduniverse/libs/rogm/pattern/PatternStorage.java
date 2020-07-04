package net.runeduniverse.libs.rogm.pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.querying.Filter;

public class PatternStorage {
	
	private Map<Class<?>, IPattern> patterns = new HashMap<>();
	
	public PatternStorage(List<String> pkts) {
		
		Reflections reflections = new Reflections(pkts.toArray(), new TypeAnnotationsScanner(), new SubTypesScanner(true));
		
		reflections.getTypesAnnotatedWith(NodeEntity.class).forEach(c->{
			this.patterns.put(c, _parse(c));
		});
		reflections.getTypesAnnotatedWith(RelationshipEntity.class).forEach(c->{
			this.patterns.put(c, _parse(c));
		});
	}
	
	public Filter createFilter(Class<?> type, int depth) {
		
		
		return null;
	}
	
	
	
	private IPattern _parse(Class<?> type) {
		if (type.isAnnotationPresent(NodeEntity.class))
			return new NodePattern();

		if (type.isAnnotationPresent(RelationshipEntity.class))
			return new RelationPattern();


		return null;
	}
}
