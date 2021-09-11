package net.runeduniverse.libs.rogm.test.dummies;

import net.runeduniverse.libs.rogm.parser.Parser.Instance;

public class DummyParserInstance implements Instance{

	@Override
	public String serialize(Object object) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T deserialize(Class<T> clazz, String value) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> void deserialize(T obj, String value) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
