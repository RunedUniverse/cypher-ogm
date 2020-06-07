package net.runeduniverse.libs.rogm.querying;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.runeduniverse.libs.rogm.lang.Cypher;
import net.runeduniverse.libs.rogm.querying.FRelation.Direction;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.FilterRelation;
import net.runeduniverse.libs.rogm.querying.IDFilter;

public class CypherTest {

	/*
		ASSERTS NOT VIABLE BECAUSE IT SOMEWHAT CHANGES THE DIRECTIONS INSIDE THE QUERIES
		!! THE QUERIES MUST BE CHECKED MANUALLY!!
	*/
	
	static Cypher cypher = new Cypher();

	static IDFilter<Integer> school;
	static FilterNode student;
	static FilterNode friends;
	static FilterRelation anyRelationToSchool;

	@Before
	public void prep() {
		school = new IDFilter<>(10);

		student = new FilterNode()
					.addLabel("HTLStudent")
					.addLabel("Maturant")
					.addRelationTo(school);

		friends = new FilterNode()
					.addLabel("Person")
					.addRelation(new FilterRelation().addLabel("Friend"), student);
		
		anyRelationToSchool = new FilterRelation(Direction.BIDIRECTIONAL).setStart(school);
	}

	@Test
	public void wrongID() {
		boolean error = false;
		try {
			cypher.buildQuery(new IDFilter<String>("defaultId"));
		} catch (Exception e) {
			error = true;
		}
		assertTrue("String is not a valid id", error);
	}
	@Test
	public void shortID() throws Exception {
		cypher.buildQuery(new IDFilter<Short>((short) 3));
	}
	@Test
	public void integerID() throws Exception {
		cypher.buildQuery(new IDFilter<Integer>(45));
	}
	@Test
	public void longID() throws Exception {
		cypher.buildQuery(new IDFilter<Long>(54l));
	}
	
	@Test
	public void matchSchool() throws Exception {
		System.out.println("[SCHOOL]\n" + cypher.buildQuery(school) + '\n');
	}

	@Test
	public void matchStudent() throws Exception {
		System.out.println("[STUDENT]\n" + cypher.buildQuery(student) + '\n');
	}

	@Test
	public void matchFriends() throws Exception {
		System.out.println("[FRIENDS]\n" + cypher.buildQuery(friends) + '\n');
	}

	@Test
	public void matchAnyRelationToSchool() throws Exception {
		System.out.println("[ANY REL]\n" + cypher.buildQuery(anyRelationToSchool) + '\n');
	}

}
