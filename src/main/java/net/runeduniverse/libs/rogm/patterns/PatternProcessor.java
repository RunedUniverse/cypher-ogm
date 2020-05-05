package net.runeduniverse.libs.rogm.patterns;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Property;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;

public class PatternProcessor {

	private List<NodePattern> nodes = new ArrayList<>();
	private List<RelationPattern> relations = new ArrayList<>();

	private PatternProcessor(List<String> packages) {

		Reflections rfl = new Reflections(new ConfigurationBuilder()
				.forPackages((String[]) packages.toArray(new String[packages.size()])).setScanners(
						new TypeAnnotationsScanner(), new FieldAnnotationsScanner(), new MethodAnnotationsScanner()));

		for (Class<?> clazz : rfl.getTypesAnnotatedWith(NodeEntity.class))
			nodes.add(classParser(clazz, clazz.getAnnotation(NodeEntity.class)));

		for (Class<?> clazz : rfl.getTypesAnnotatedWith(RelationshipEntity.class)) {
			RelationPattern relp = new RelationPattern(clazz, clazz.getAnnotation(RelationshipEntity.class));
			// TODO: parse relationships

			relations.add(relp);
		}
	}

	private NodePattern classParser(Class<?> clazz, NodeEntity entity) {
		NodePattern node = new NodePattern(clazz, entity);

		for (Field v : clazz.getFields()) {
			String tag = v.getName();
			// TODO: do sth if needed
			/*
			 * if(clazz.isAnnotationPresent(Relationship.class)) { Relationship r =
			 * clazz.getAnnotation(Relationship.class); continue; }
			 */

			Property p = v.getAnnotation(Property.class);
			switch (entity.mode()) {
			case EXPLICIT:
				if (p == null)
					continue;
			case IMPLICIT:
			default:
				if (p != null && p.tag().trim() != "")
					tag = p.tag().trim();

				node.addField(tag, v);
				break;
			}
		}

		return node;
	}

	// types
	public class PatternProcessorBuilder {
		private List<String> packages = new ArrayList<>();

		// chainable methods
		public PatternProcessorBuilder addPackage(String pkg) {
			this.packages.add(pkg);
			return this;
		}

		public PatternProcessor build() {
			return new PatternProcessor(this.packages);
		}

	}
}
