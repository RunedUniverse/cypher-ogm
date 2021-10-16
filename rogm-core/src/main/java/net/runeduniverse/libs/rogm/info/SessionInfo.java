package net.runeduniverse.libs.rogm.info;

import lombok.Getter;
import net.runeduniverse.libs.logging.logs.CompoundTree;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.pipeline.APipelineFactory;

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
