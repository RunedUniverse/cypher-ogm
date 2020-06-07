package net.runeduniverse.libs.rogm;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.enums.DatabaseType;

@Getter
public class Configuration {

	private DatabaseType dbType;
	@Setter
	private String protocol;
	@Setter
	private int port;
	@Setter
	private String user;
	@Setter
	private String password;

	public Configuration(DatabaseType type) {
		this.dbType = type;
		type.getModule().prepare(this);
	}
}
