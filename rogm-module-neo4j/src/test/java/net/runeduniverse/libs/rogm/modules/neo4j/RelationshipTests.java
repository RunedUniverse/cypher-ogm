/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.libs.rogm.modules.neo4j;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Relationship;
import net.runeduniverse.libs.rogm.annotations.RelationshipEntity;
import net.runeduniverse.libs.rogm.annotations.StartNode;
import net.runeduniverse.libs.rogm.annotations.TargetNode;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.FilterRelation;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;
import net.runeduniverse.libs.rogm.querying.IFilter;
import net.runeduniverse.libs.rogm.test.AConfigTest;
import net.runeduniverse.libs.rogm.test.model.*;
import net.runeduniverse.libs.rogm.test.model.relations.*;

public class RelationshipTests extends AConfigTest {

	public RelationshipTests() {
		super(new Neo4jConfiguration(""));
	}

	@Test
	@Tag("system")
	public void testIfClassIsNode() {
		Boolean res = checkIfClassIsNode(Artist.class);
		assertTrue(res);
	}

	@Test
	@Tag("system")
	public void testIfClassIsRelationship() {
		Boolean res = checkIfClassIsRelationshipEntity(ActorPlaysPersonRelation.class);
		assertTrue(res);
	}

	@Test
	@Tag("system")
	public void testUsingActorClass() throws Exception {
		System.out.println(_build(Actor.class));
	}

	@Test
	@Tag("system")
	public void testUsingGameClass() throws Exception {
		System.out.println(_build(Game.class));
	}

	@Test
	@Tag("system")
	public void testUsingCompanyClass() throws Exception {
		System.out.println(_build(Company.class));
	}

	private String _build(Class<?> clazz) throws Exception {
		return '[' + clazz.getSimpleName() + "]\n" + iLanguage.load(giveFilterNodeOrRelation(clazz, false)) + '\n';
	}

	Map<Class<?>, IFilter> classMap = new HashMap<Class<?>, IFilter>();

	private IFilter giveFilterNodeOrRelation(Class<?> clazz, boolean isChild) throws Exception {

		// System.out.println("ClassMapContent:" + classMap);

		if (classMap.containsKey(clazz)) {
			return classMap.get(clazz);
		}

		// Create FilterNode
		if (checkIfClassIsNode(clazz)) {
			return createFilterNode(clazz, isChild);
		} else {
			// Create FilterRelation
			if (checkIfClassIsRelationshipEntity(clazz)) {
				return createFilterRelation(clazz, isChild);
			}
			// I have no idea what you gave me so go away!
			else {
				return null;
			}
		}
	}

	private IFNode createFilterNode(Class<?> clazz, boolean isChild) throws Exception {
		FilterNode fn = new FilterNode().setReturned(true)
				.setOptional(isChild);
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
				String label = r.label()
						.isEmpty() ? ff.getName() : r.label();
				if (isOfTypeCollection(ffClazz)) {
					ffClazz = getClassFromCollectionField(ff);
				}
				if (ffClazz.isAnnotationPresent(RelationshipEntity.class)) {
					FilterRelation fr = (FilterRelation) giveFilterNodeOrRelation(ffClazz, true);
					if (fr.getLabels()
							.isEmpty())
						fr.addLabel(label);
					fn.addRelation(fr);
				} else {
					FilterRelation fr = new FilterRelation(r.direction()).addLabel(label)
							.setReturned(true)
							.setOptional(true);
					fn.addRelation(fr, (IFNode) giveFilterNodeOrRelation(ffClazz, true));
				}
			}
		}
		return fn;
	}

	private IFRelation createFilterRelation(Class<?> clazz, boolean isChild) throws Exception {
		Boolean startNode = false, endNode = false;
		FilterRelation fr = new FilterRelation().setReturned(true)
				.setOptional(isChild);
		classMap.put(clazz, fr);

		RelationshipEntity re = clazz.getAnnotation(RelationshipEntity.class);
		fr.setDirection(re.direction());
		if (!re.label()
				.isEmpty())
			fr.addLabel(re.label());

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Class<?> fieldClass = field.getType();
			if (!(field.isAnnotationPresent(StartNode.class) || field.isAnnotationPresent(TargetNode.class)))
				continue;
			if (isOfTypeCollection(fieldClass))
				throw new Exception("A Collection inside RelationshipEntity is not allowed!!!");
			if (field.isAnnotationPresent(StartNode.class)) {
				startNode = true;
				fr.setStart((IFNode) giveFilterNodeOrRelation(fieldClass, true));
			} else if (field.isAnnotationPresent(TargetNode.class)) {
				endNode = true;
				fr.setTarget((IFNode) giveFilterNodeOrRelation(fieldClass, true));
			}
		}
		if (!(startNode && endNode))
			throw new Exception(
					"A RelationshipEntity needs a Field with the StartNode Annotation and a Field with the TargetNode Annotation!!!");
		return fr;
	}

	private Class<?> getClassFromCollectionField(Field collectionClass) throws ClassNotFoundException {
		return (Class<?>) ((Type[]) ((ParameterizedType) collectionClass.getGenericType()).getActualTypeArguments())[0];
	}

	private Boolean isOfTypeCollection(Class<?> maybeCollection) {
		return Collection.class.isAssignableFrom(maybeCollection);
	}

	private <T> void getLabelsForClass(Class<T> type, List<String> labels) {
		labels.add(type.getSimpleName());
		if (Modifier.isAbstract(type.getSuperclass()
				.getModifiers()) || type.getSuperclass() == Object.class)
			return;
		getLabelsForClass(type.getSuperclass(), labels);
	}

	private Boolean checkIfClassIsRelationshipEntity(Class<?> clazz) {
		return clazz.isAnnotationPresent(RelationshipEntity.class);
	}

	private Boolean checkIfClassIsNode(Class<?> clazz) {
		return clazz.isAnnotationPresent(NodeEntity.class);
	}

}
