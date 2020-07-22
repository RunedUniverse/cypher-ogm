package net.runeduniverse.libs.rogm;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.buffer.BasicBuffer;
import net.runeduniverse.libs.rogm.buffer.IBuffer;

@Getter
public class Configuration {

	private DatabaseType dbType;
	private List<String> pkgs = new ArrayList<>();
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
	@Setter
	private IBuffer buffer = new BasicBuffer();

	public Configuration(DatabaseType type, String uri) {
		this.dbType = type;
		this.uri = uri;
		type.getModule().prepare(this);
	}

	public Configuration addPackage(String pkg) {
		this.pkgs.add(pkg);
		return this;
	}

	public Configuration addPackage(List<String> pkgs) {
		this.pkgs.addAll(pkgs);
		return this;
	}
}
