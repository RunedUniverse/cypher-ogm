package net.runeduniverse.libs.rogm.model;

import net.runeduniverse.libs.rogm.annotations.NodeEntity;
import net.runeduniverse.libs.rogm.annotations.Property;

@NodeEntity
public class AnsaStudent extends Student {
	public AnsaStudent(String address) {
		super(address);
	}
	@Property
	private Integer grade = 1;
	
}
