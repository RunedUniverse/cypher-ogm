package net.runeduniverse.libs.rogm.test.dummies;

import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.modules.IdTypeResolver;
import net.runeduniverse.libs.rogm.parser.Parser;

public class DummyParser implements Parser {

	@Override
	public Instance build(Logger logger, IdTypeResolver resolver) {
		return new DummyParserInstance();
	}

}
