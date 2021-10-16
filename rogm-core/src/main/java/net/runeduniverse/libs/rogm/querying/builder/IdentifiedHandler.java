package net.runeduniverse.libs.rogm.querying.builder;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.querying.IIdentified;

@AllArgsConstructor
public class IdentifiedHandler implements IIdentified<Serializable>, NoFilterType {
	@Getter
	@Setter
	private Serializable id;

}
