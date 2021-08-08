package net.runeduniverse.libs.rogm.info;

import lombok.Getter;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.pipeline.transaction.ATransactionFactory;

@Getter
public class SessionInfo {
	private final PackageInfo pkgInfo;
	private final ConnectionInfo[] conInfos;
	private final Class<? extends ATransactionFactory> builderClass;
	private final Class<? extends IBuffer> bufferClass;

	public SessionInfo(Class<? extends ATransactionFactory> builderClass, Class<? extends IBuffer> bufferClass,
			PackageInfo pkgInfo, ConnectionInfo... conInfos) {
		this.pkgInfo = pkgInfo;
		this.conInfos = conInfos;
		this.builderClass = builderClass;
		this.bufferClass = bufferClass;
	}
}
