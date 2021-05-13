package net.runeduniverse.libs.rogm.pipeline;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.PassiveModule;
import net.runeduniverse.libs.rogm.parser.Parser;

public class EntityFactory implements IFactory{
	final Assembler assembler;
	final IBuffer buffer;
	final Language.Instance lang;
	final Parser.Instance paser;
	final Module.Instance<?> module;
	final Collection<PassiveModule> passiveModules = new HashSet<>();

	public EntityFactory(final Assembler assembler, final IBuffer buffer, final Language.Instance lang,
			final Parser.Instance paser, final Module.Instance<?> module, final PassiveModule... passiveModules) {
		this.assembler = assembler;
		this.buffer = buffer;
		this.lang = lang;
		this.paser = paser;
		this.module = module;
		this.passiveModules.addAll(Arrays.asList(passiveModules));
	}
}
