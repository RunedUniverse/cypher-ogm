package net.runeduniverse.libs.rogm;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.rogm.buffer.BasicBuffer;
import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.entities.scanner.TypeScanner;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.PassiveModule;
import net.runeduniverse.libs.rogm.parser.Parser;

@Getter
public class Configuration {

	protected final List<String> pkgs = new ArrayList<>();
	protected final List<ClassLoader> loader = new ArrayList<>();
	protected final List<TypeScanner> scanner = new ArrayList<>();
	private final Parser parser;
	protected final Language lang;
	protected final Module module;

	@Setter
	protected String uri;
	@Setter
	protected Logger logger = null;
	@Setter
	protected Level loggingLevel = null;
	@Setter
	protected String protocol;
	@Setter
	protected int port;
	@Setter
	protected String user;
	@Setter
	protected String password;
	@Setter
	protected IBuffer buffer = new BasicBuffer();

	public Configuration(Parser parser, Language lang, Module module, String uri) {
		this.parser = parser;
		this.lang = lang;
		this.module = module;
		this.uri = uri;
	}

	public Configuration addPackage(String pkg) {
		this.pkgs.add(pkg);
		return this;
	}

	public Configuration addPackage(List<String> pkgs) {
		this.pkgs.addAll(pkgs);
		return this;
	}

	public Configuration addClassLoader(ClassLoader loader) {
		this.loader.add(loader);
		return this;
	}

	public Configuration addClassLoader(List<ClassLoader> loader) {
		this.loader.addAll(loader);
		return this;
	}

	public Configuration configure(PassiveModule passivemodule) {
		if (passivemodule.getPatternScanner() != null)
			this.scanner.addAll(passivemodule.getPatternScanner());
		return this;
	}

	public Parser.Instance buildParserInstance() {
		return this.parser.build(this);
	}

	public Module.Instance<?> buildModuleInstance() {
		return this.module.build(this);
	}

	public Language.Instance buildLanguageInstance(Parser.Instance parser) {
		return this.lang.build(parser, this.module);
	}
}
