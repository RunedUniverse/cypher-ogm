package net.runeduniverse.libs.rogm.test.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.querying.QueryBuilder;
import net.runeduniverse.libs.rogm.test.ATest;
import net.runeduniverse.libs.rogm.test.model.Company;

public class QueryBuilderTest extends ATest {
	private QueryBuilder builder;

	public QueryBuilderTest(Configuration cnf) {
		super(cnf);
	}

	@Test
	@Tag("system")
	private void test() {
		builder.node()
				.where(Company.class);
	}
}
