package net.runeduniverse.libs.rogm.querying.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.querying.IReturned;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReturnedHandler implements IReturned, NoFilterType {

	private boolean returned = false;
}
