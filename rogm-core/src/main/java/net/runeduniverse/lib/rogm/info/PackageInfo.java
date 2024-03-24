/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.rogm.info;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.lib.rogm.Configuration;

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
