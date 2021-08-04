package net.runeduniverse.libs.rogm.querying.builder;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;

@Getter
public class NodeFilter extends AProxyFilter<NodeFilter> implements IFNode {

	private final Set<IFRelation> relations = new HashSet<>();

	public NodeFilter() {
		this.instance = this;
	}

}
