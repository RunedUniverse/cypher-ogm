/*
 * Copyright © 2022 VenaNocta (venanocta@gmail.com)
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

import lombok.Getter;
import net.runeduniverse.lib.rogm.buffer.IBuffer;
import net.runeduniverse.lib.rogm.pipeline.APipelineFactory;
import net.runeduniverse.lib.utils.logging.logs.CompoundTree;

@Getter
public class SessionInfo {
	private final PackageInfo pkgInfo;
	private final ConnectionInfo[] conInfos;
	private final Class<? extends APipelineFactory<?>> builderClass;
	private final Class<? extends IBuffer> bufferClass;

	public SessionInfo(Class<? extends APipelineFactory<?>> builderClass, Class<? extends IBuffer> bufferClass,
			PackageInfo pkgInfo, ConnectionInfo... conInfos) {
		this.pkgInfo = pkgInfo;
		this.conInfos = conInfos;
		this.builderClass = builderClass;
		this.bufferClass = bufferClass;
	}

	@Override
	public String toString() {
		CompoundTree tree = new CompoundTree("Initializing Session");
		tree.append(new CompoundTree("PipelineFactory:").append(this.builderClass.getSimpleName()));
		tree.append(new CompoundTree("Buffer:").append(this.bufferClass.getSimpleName()));

		CompoundTree pkgs = new CompoundTree("Model Packages:");
		for (String p : this.pkgInfo.getPkgs())
			pkgs.append(p);
		tree.append(pkgs);

		for (ConnectionInfo conInfo : this.conInfos)
			tree.append(new CompoundTree("Database Module: " + conInfo.getModule()
					.getClass()
					.getSimpleName()).append("Uri:      " + conInfo.getUri())
							.append("Protocol: " + conInfo.getProtocol())
							.append("Port:     " + conInfo.getPort())
							.append("User:     " + conInfo.getUser()));
		return tree.toString();
	}
}
