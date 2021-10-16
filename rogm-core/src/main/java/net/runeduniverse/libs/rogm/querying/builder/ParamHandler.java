package net.runeduniverse.libs.rogm.querying.builder;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.runeduniverse.libs.logging.logs.CompoundTree;
import net.runeduniverse.libs.rogm.querying.IParameterized;

@NoArgsConstructor
public class ParamHandler implements IParameterized, NoFilterType, ITraceable {
	@Getter
	private Map<String, Object> params = new HashMap<>();

	public ParamHandler addParam(String label, Object value) {
		this.params.put(label, value);
		return this;
	}

	@Override
	public void toRecord(CompoundTree tree) {
		this.params.forEach((l, v) -> {
			tree.append("WHERE", l + " = " + v);
		});
	}
}
