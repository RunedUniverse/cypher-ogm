package net.runeduniverse.libs.rogm.info;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
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
		List<String> msg = new ArrayList<String>();
		msg.add("Initializing Session");
		for (ConnectionInfo conInfo : this.conInfos) {
			msg.add("Database Module: " + conInfo.getModule()
					.getClass()
					.getSimpleName());
			msg.add(" ├ Uri:      " + conInfo.getUri());
			msg.add(" ├ Protocol: " + conInfo.getProtocol());
			msg.add(" ├ Port:     " + conInfo.getPort());
			msg.add(" └ User:     " + conInfo.getUser());
		}
		msg.add("Model Packages:");
		for (Iterator<String> pkgIter = this.pkgInfo.getPkgs()
				.iterator(); pkgIter.hasNext();) {
			String s = pkgIter.next();
			msg.add((pkgIter.hasNext() ? " ├ " : " └ ") + s);
		}
		msg.add("TransactionBuilder:");
		msg.add(" └ " + this.builderClass.getSimpleName());
		msg.add("Buffer:");
		msg.add(" └ " + this.bufferClass.getSimpleName());

		return String.join("\n\t", msg);
	}
}
