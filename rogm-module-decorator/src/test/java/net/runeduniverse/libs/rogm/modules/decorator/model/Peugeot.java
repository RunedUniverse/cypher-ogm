package net.runeduniverse.libs.rogm.modules.decorator.model;

import net.runeduniverse.libs.rogm.modules.decorator.annotations.Decorated;

public class Peugeot extends Car {
	
	@Decorated
	private Car car;

}
