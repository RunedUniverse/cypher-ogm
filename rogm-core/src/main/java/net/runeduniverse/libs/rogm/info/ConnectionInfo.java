package net.runeduniverse.libs.rogm.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.rogm.Configuration;
import net.runeduniverse.libs.rogm.modules.Module;

@Getter
@Setter
@NoArgsConstructor
public class ConnectionInfo {

	private Module module;
	private String uri;
	private String protocol;
	private int port;
	private String user;

	public ConnectionInfo(Configuration cnf) {
		this.module = cnf.getModule();
		this.uri = cnf.getUri();
		this.protocol = cnf.getProtocol();
		this.port = cnf.getPort();
		this.user = cnf.getUser();
	}
}
