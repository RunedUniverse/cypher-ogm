package net.runeduniverse.libs.rogm.querying;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.runeduniverse.libs.rogm.ATest;
import net.runeduniverse.libs.rogm.DatabaseType;
import net.runeduniverse.libs.rogm.annotations.Direction;
import net.runeduniverse.libs.rogm.querying.FilterNode;
import net.runeduniverse.libs.rogm.querying.FilterRelation;

public class CypherTest extends ATest {

	/*
	 * ASSERTS NOT VIABLE BECAUSE IT SOMEWHAT CHANGES THE DIRECTIONS INSIDE THE
	 * QUERIES !! THE QUERIES MUST BE CHECKED MANUALLY!!
	 */

	public CypherTest() {
		super(DatabaseType.Neo4j);
	}

	static FilterNode school;
	static FilterNode student;
	static FilterNode friends;
	static FilterRelation anyRelationToSchool;
	static FilterNode city = new FilterNode();

	@Before
	public void prep() {
		school = new FilterNode(10);

		student = new FilterNode().addLabel("HTLStudent").addLabel("Maturant").addRelationTo(school);

		friends = new FilterNode().addLabel("Person").addRelation(new FilterRelation().addLabel("Friend"), student);

		anyRelationToSchool = new FilterRelation(Direction.BIDIRECTIONAL).setStart(school);

		Map<String, Object> infos = new HashMap<>();
		infos.put("Hospital", "The Royal London Hospital");
		infos.put("Parks", new String[] { "Hyde Park", "Greenwich Park", "Regent's Park" });
		city.addLabel("City").addParam("name", "London").addParam("Citizens", 9787426).addParam("infos", infos);
	}

	@Test
	public void wrongID() {
		boolean error = false;
		try {
			iLanguage.query(new FilterNode("defaultId"));
		} catch (Exception e) {
			error = true;
		}
		assertTrue("String is not a valid id", error);
	}

	@Test
	public void shortID() throws Exception {
		iLanguage.query(new FilterNode((short) 3));
	}

	@Test
	public void integerID() throws Exception {
		iLanguage.query(new FilterNode(45));
	}

	@Test
	public void longID() throws Exception {
		iLanguage.query(new FilterNode(54l));
	}

	// MATCHES
	@Test
	public void matchSchool() throws Exception {
		System.out.println("[SCHOOL]\n" + iLanguage.query(school) + '\n');
	}

	@Test
	public void matchStudent() throws Exception {
		System.out.println("[STUDENT]\n" + iLanguage.query(student) + '\n');
	}

	@Test
	public void matchFriends() throws Exception {
		System.out.println("[FRIENDS]\n" + iLanguage.query(friends) + '\n');
	}

	@Test
	public void matchAnyRelationToSchool() throws Exception {
		System.out.println("[ANY REL]\n" + iLanguage.query(anyRelationToSchool) + '\n');
	}

	@Test
	public void matchCity() throws Exception {
		System.out.println("[CITY]\n" + iLanguage.query(city) + '\n');
	}

	// CREATE
	// no id defined => merge
	// id defined => create/merge
	@Test
	public void createArtist() throws Exception {
		// iLanguage.buildInsert();
	}

	// UPDATE
}
