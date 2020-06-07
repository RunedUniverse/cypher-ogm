package net.runeduniverse.libs.rogm;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.runeduniverse.libs.rogm.lang.Cypher;
import net.runeduniverse.libs.rogm.querying.FRelation.Direction;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.FilterRelation;
import net.runeduniverse.libs.rogm.querying.IDFilter;

public class CypherFilterTest {

	static Cypher cypher = new Cypher();

	static IDFilter school;
	static FilterNode student;
	static FilterNode friends;
	static FilterRelation anyRelationToSchool;

	@Before
	public void prep() {
		school = new IDFilter(10);

		student = new FilterNode()
					.addLabel("HTLStudent")
					.addLabel("Maturant")
					.addRelationTo(school);

		friends = new FilterNode()
					.addLabel("Person");
		friends.addRelation(
				new FilterRelation()
					.addLabel("Friend")
					.setStart(student)
					.setTarget(friends)
				);
		anyRelationToSchool = new FilterRelation(Direction.BIDIRECTIONAL).setStart(school);
	}

	@Test
	public void matchSchool() throws Exception {
		System.out.println("[SCHOOL]\n" + cypher.buildQuery(school) + '\n');
		assertEquals("MATCH (a)\nWHERE id(a)=10\nRETURN a;", cypher.buildQuery(school));
	}

	@Test
	public void matchStudent() throws Exception {
		System.out.println("[STUDENT]\n" + cypher.buildQuery(student) + '\n');
		assertEquals("MATCH (c)\nWHERE id(c)=10\nMATCH (a:HTLStudent:Maturant {})\n"
				+ "MATCH (a)-[b {}]->(c)\nRETURN a;", cypher.buildQuery(student));
	}

	@Test
	public void matchFriends() throws Exception {
		System.out.println("[FRIENDS]\n" + cypher.buildQuery(friends) + '\n');
		assertEquals("MATCH (c:HTLStudent:Maturant {})-[b:Friend {}]-(a:Person {})\nMATCH (e)\n"
				+ "WHERE id(e)=10\nMATCH (c)-[d {}]->(e)\nRETURN a;", cypher.buildQuery(friends));
	}

	@Test
	public void matchAnyRelationToSchool() throws Exception {
		System.out.println("[ANY REL]\n" + cypher.buildQuery(anyRelationToSchool) + '\n');
		assertEquals("MATCH (b)-[a {}]-()\nMATCH (b)\nWHERE id(b)=10\nRETURN a;",
				cypher.buildQuery(anyRelationToSchool));
	}

}
