package net.runeduniverse.libs.rogm.modules.decorator.model;

import net.runeduniverse.libs.rogm.modules.decorator.annotations.Decorated;

public class ElectricCar extends Car {
	
	@Decorated
	private Car car;

}
