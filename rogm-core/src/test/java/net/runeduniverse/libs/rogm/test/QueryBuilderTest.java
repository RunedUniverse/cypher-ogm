package net.runeduniverse.libs.rogm.test;

import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.rogm.test.model.Company;

public class QueryBuilderTest extends ATest {
	private QueryBuilder builder;

	public QueryBuilderTest(Configuration cnf) {
		super(cnf);
	}

	@Test
	private void test() {
		builder.search(Company.class);
	}
}
