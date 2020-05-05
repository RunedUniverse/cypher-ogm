package net.runeduniverse.libs.rogm.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.runeduniverse.libs.rogm.annotations.*;

@NoArgsConstructor
@NodeEntity(label = "SCHUELER")
@Getter
public class Student {

	@Id
	@GeneratedValue
	private long id;

	@Property
	private String address;

	
	public Student(String address) {
		this.address = address;
	}

	
	@PostLoad
	public void postLoad(Object o) {

	}

}
