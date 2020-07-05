package net.runeduniverse.libs.rogm;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;

import net.runeduniverse.libs.rogm.annotations.EndNode;
import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.annotations.StartNode;
import net.runeduniverse.libs.rogm.lang.Cypher;
import net.runeduniverse.libs.rogm.model.Actor;
import net.runeduniverse.libs.rogm.model.Artist;
import net.runeduniverse.libs.rogm.model.relations.ActorPlaysPersonRelation;
import net.runeduniverse.libs.rogm.parser.JSONParser;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.FilterRelation;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFilter;

public class RelationshipTests {

	@Before
	public void prepare() {
	}

	@Test
	public void testIfClassIsNode() {
		Boolean res = checkIfClassIsNode(Artist.class);
		assertTrue(res);
	}

	@Test
	public void testIfClassIsRelationship() {
		Boolean res = checkIfClassIsRelationshipEntity(ActorPlaysPersonRelation.class);
		assertTrue(res);
	}

	static Cypher cypher = new Cypher();
	static Parser parser = new JSONParser();

	@Test
	public void testUsingActorClass() throws Exception {
		System.out.println(cypher.buildQuery(giveFilterNodeOrRelation(Actor.class), parser));
	}

	Map<Class<?>, IFilter> classMap = new HashMap<Class<?>, IFilter>();

	private IFilter giveFilterNodeOrRelation(Class<?> clazz) throws Exception {

		System.out.println("ClassMapContent:" + classMap);

		if (classMap.containsKey(clazz)) {
			return classMap.get(clazz);
		}

		// Create FilterNode
		if (checkIfClassIsNode(clazz)) {
			FilterNode fn = new FilterNode();
			classMap.put(clazz, fn);
			List<String> labels = new ArrayList<String>();
			getLabelsForClass(clazz, labels);
			fn.addLabels(labels);
			Field[] f = clazz.getDeclaredFields();
			for (int i = 0; i < f.length; i++) {
				Field ff = f[i];
				if (ff.isAnnotationPresent(Relationship.class)) {
					Class<?> ffClazz = ff.getType();
					Relationship r = ff.getAnnotation(Relationship.class);
					String label = r.label().isEmpty() ? ff.getName() : r.label();
					if (isOfTypeCollection(ffClazz)) {
						/*
						 * System.out.println("TEST1:"+ff); System.out.println("TEST2:"+ffClazz);
						 * System.out.println("TEST3:"+ff.getGenericType());
						 * System.out.println("TEST4:"+ff.getGenericType().getTypeName());
						 * System.out.println("TEST5:"+((ParameterizedType)
						 * ff.getGenericType()).getRawType());
						 */
						ffClazz = getClassFromCollectionField(ff);
					}
					if (ffClazz.isAnnotationPresent(RelationshipEntity.class)) {
						FilterRelation fr = (FilterRelation) giveFilterNodeOrRelation(ffClazz);
						if (fr.getLabels().isEmpty())
							fr.addLabel(label);
						fn.addRelation(fr);
					} else {
						FilterRelation fr = new FilterRelation(r.direction()).addLabel(label);
						fn.addRelation(fr, (IFNode) giveFilterNodeOrRelation(ffClazz));
					}
				}
			}
			return fn;
		} else {
			// Create FilterRelation
			if (checkIfClassIsRelationshipEntity(clazz)) {
				Boolean startNode = false, endNode = false;
				FilterRelation fr = new FilterRelation();
				classMap.put(clazz, fr);
				RelationshipEntity re = clazz.getAnnotation(RelationshipEntity.class);
				fr.setDirection(re.direction());
				if (!re.label().isEmpty())
					fr.addLabel(re.label());
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					Class<?> fieldClass = field.getType();
					if (!(field.isAnnotationPresent(StartNode.class) || field.isAnnotationPresent(EndNode.class)))
						continue;
					if (isOfTypeCollection(fieldClass))
						throw new Exception("A Collection inside RelationshipEntity is not allowed!!!");
					if (field.isAnnotationPresent(StartNode.class)) {
						startNode = true;
						fr.setStart((IFNode) giveFilterNodeOrRelation(fieldClass));
					} else if (field.isAnnotationPresent(EndNode.class)) {
						endNode = true;
						fr.setTarget((IFNode) giveFilterNodeOrRelation(fieldClass));
					}
				}
				if (!(startNode && endNode))
					throw new Exception(
							"A RelationshipEntity needs a Field with the StartNode Annotation and a Field with the EndNode Annotation!!!");
				return fr;
			}
			// I have no idea what you gave me so go away!
			else {
				return null;
			}
		}
	}

	private Class<?> getClassFromCollectionField(Field collectionClass) throws ClassNotFoundException {
		ParameterizedType type = (ParameterizedType) collectionClass.getGenericType();
		// System.out.println("TTEST1:"+type);
		// System.out.println("TTEST2:"+Class.forName("net.runeduniverse.libs.rogm.model.Actor"));
		Type[] typeArgs = type.getActualTypeArguments();
		// System.out.println("TTEST3:"+typeArgs);
		/*
		 * for (Type type2 : typeArgs) { System.out.println("TTEST4:"+type2); }
		 */
		// System.out.println("TTEST5:"+typeArgs[0]);
		Type t = typeArgs[0];
		// System.out.println("TTEST6:"+t);
		Class<?> c = (Class<?>) t;
		// System.out.println("TTEST7:"+c);
		/*
		 * ParameterizedType type = (ParameterizedType)
		 * collectionClass.getGenericType(); return (Class<?>)
		 * type.getActualTypeArguments()[0];
		 */
		return c;
	}

	private Boolean isOfTypeCollection(Class<?> maybeCollection) {
		return Collection.class.isAssignableFrom(maybeCollection);
	}

	private <T> void getLabelsForClass(Class<T> type, List<String> labels) {
		labels.add(type.getSimpleName());
		if (Modifier.isAbstract(type.getSuperclass().getModifiers()) || type.getSuperclass() == Object.class)
			return;
		getLabelsForClass(type.getSuperclass(), labels);
	}

	private Boolean checkIfClassIsRelationshipEntity(Class<?> clazz) {
		return clazz.isAnnotationPresent(RelationshipEntity.class);
	}

	private Boolean checkIfClassIsNode(Class<?> clazz) {
		return clazz.isAnnotationPresent(NodeEntity.class);
	}

	@After
	public void close() {
	}

}
