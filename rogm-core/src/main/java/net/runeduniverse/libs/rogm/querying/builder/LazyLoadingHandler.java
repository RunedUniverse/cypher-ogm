package net.runeduniverse.libs.rogm.querying.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.querying.ILazyLoading;

@Getter
@Setter
@AllArgsConstructor
public class LazyLoadingHandler implements ILazyLoading, NoFilterType {

	private boolean lazy = false;

}
