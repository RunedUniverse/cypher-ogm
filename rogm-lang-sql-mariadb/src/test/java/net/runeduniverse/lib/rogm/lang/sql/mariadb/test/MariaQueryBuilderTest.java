package net.runeduniverse.lib.rogm.lang.sql.mariadb.test;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.rogm.querying.IFilter;
import net.runeduniverse.lib.rogm.test.AQueryBuilderTest;
import net.runeduniverse.lib.rogm.test.model.Person;
import net.runeduniverse.lib.rogm.test.system.TestEntity;

public class MariaQueryBuilderTest extends AQueryBuilderTest {
	@Test
	@Tag("system")
	public void selectPerson() throws Exception {
		IFilter filter = this.qryBuilder
		.node()
		.where(Person.class)
		.whereParam("firstName", "Shawn")
		.whereParam("lastName", "James")
		.getResult();

		TestEntity.infoTesting(this.logger, filter);
		
		System.out.println("[SELECT SHAWN]\n" + this.iLanguage.load(filter).qry() + '\n');
	}
}
