package net.runeduniverse.libs.rogm.querying;

import java.util.Map;

public interface IParameterized extends IFilter {
	Map<String, Object> getParams();
}
