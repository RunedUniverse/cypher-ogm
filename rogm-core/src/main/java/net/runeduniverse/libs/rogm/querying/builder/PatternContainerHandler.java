package net.runeduniverse.libs.rogm.querying.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.logging.logs.CompoundTree;
import net.runeduniverse.libs.rogm.pattern.IBaseQueryPattern;
import net.runeduniverse.libs.rogm.pattern.IPattern;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatternContainerHandler implements IPattern.IPatternContainer, NoFilterType, ITraceable {

	private IBaseQueryPattern<?> pattern;

	@Override
	public void toRecord(CompoundTree tree) {
		if (this.pattern != null)
			tree.append("PATTERN", this.pattern.getClass()
					.getCanonicalName());
	}
}
