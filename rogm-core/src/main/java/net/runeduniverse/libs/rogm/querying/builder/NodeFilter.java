package net.runeduniverse.libs.rogm.querying.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import net.runeduniverse.libs.rogm.querying.IFNode;
import net.runeduniverse.libs.rogm.querying.IFRelation;

@Getter
public class NodeFilter extends AProxyFilter<NodeFilter> implements IFNode {

	private final List<IFRelation> relations = new ArrayList<>();
	
	public NodeFilter(Map<Class<?>, Object> handler) {
		super(handler);
		this.instance = this;
	}
	
	

}
