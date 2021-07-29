package net.runeduniverse.libs.rogm.querying.builder;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;

@Getter
public class NodeFilter extends AProxyFilter<NodeFilter> implements IFNode {

	private final List<IFRelation> relations = new ArrayList<>();

	public NodeFilter() {
		this.instance = this;
	}

}
