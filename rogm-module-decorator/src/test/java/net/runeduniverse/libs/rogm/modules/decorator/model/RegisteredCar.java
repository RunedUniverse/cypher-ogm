package net.runeduniverse.libs.rogm.modules.decorator.model;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.modules.decorator.annotations.Decorated;

@Getter
@Setter
public class RegisteredCar extends Car {
	
	@Decorated
	private Car car;

	String registryPlate;
}
