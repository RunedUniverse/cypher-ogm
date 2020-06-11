package net.runeduniverse.libs.rogm;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Configuration {

	private DatabaseType dbType;
	@Setter
	private String uri;
	@Setter
	private String protocol;
	@Setter
	private int port;
	@Setter
	private String user;
	@Setter
	private String password;

	public Configuration(DatabaseType type, String uri) {
		this.dbType = type;
		this.uri = uri;
		type.getModule().prepare(this);
	}
}
