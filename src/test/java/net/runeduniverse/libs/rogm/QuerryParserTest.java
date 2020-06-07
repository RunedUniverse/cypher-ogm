package net.runeduniverse.libs.rogm;

import org.junit.Before;
import org.junit.Test;

import net.runeduniverse.libs.rogm.lang.Cypher;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.FilterRelation;
import net.runeduniverse.libs.rogm.querying.IDFilter;

public class QuerryParserTest {

	static Cypher cypher = new Cypher();
	
	static IDFilter school;
	static FilterNode student;
	static FilterNode friends;
	
	@Before
	public void prep() {
		school = new IDFilter(10);
		student = new FilterNode()
				.addLabel("HTLStudent")
				.addLabel("Maturant")
				.addRelation(school);
		friends = new FilterNode()
				.addLabel("Person");
		friends.addRelation(
						new FilterRelation()
						.addLabel("Friend")
						.setStart(student)
						.setTarget(friends)
						);
	}
	
	@Test
	public void match0() throws Exception {
		System.out.println("[QUERY] SCHOOL");
		System.out.println(cypher.buildQuery(school));
		System.out.println();
	}
	@Test
	public void match1() throws Exception {
		System.out.println("[QUERY] STUDENT");
		System.out.println(cypher.buildQuery(student));
		System.out.println();
	}
	@Test
	public void match2() throws Exception {
		System.out.println("[QUERY] FRIENDS");
		System.out.println(cypher.buildQuery(friends));
		System.out.println();
	}
	
}
