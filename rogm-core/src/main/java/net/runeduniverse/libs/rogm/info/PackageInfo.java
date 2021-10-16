package net.runeduniverse.libs.rogm.info;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.Configuration;

@Getter
@NoArgsConstructor
public class PackageInfo {
	private final Set<String> pkgs = new HashSet<>();
	private final Set<ClassLoader> loader = new HashSet<>();
	@Setter
	private Level loggingLevel = null;

	public PackageInfo(Configuration cnf) {
		this.pkgs.addAll(cnf.getPkgs());
		this.loader.addAll(cnf.getLoader());
		if (this.loggingLevel == null || this.loggingLevel.intValue() > cnf.getLoggingLevel()
				.intValue())
			this.loggingLevel = cnf.getLoggingLevel();
	}

	public PackageInfo merge(PackageInfo info) {
		this.pkgs.addAll(info.getPkgs());
		this.loader.addAll(info.getLoader());
		if (this.loggingLevel == null || this.loggingLevel.intValue() > info.getLoggingLevel()
				.intValue())
			this.loggingLevel = info.getLoggingLevel();
		return this;
	}
}
