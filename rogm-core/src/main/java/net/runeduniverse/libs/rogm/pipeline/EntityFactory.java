package net.runeduniverse.libs.rogm.pipeline;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;

import net.runeduniverse.libs.rogm.buffer.IBuffer;
import net.runeduniverse.libs.rogm.lang.Language;
import net.runeduniverse.libs.rogm.logging.UniversalLogger;
import net.runeduniverse.libs.rogm.modules.Module;
import net.runeduniverse.libs.rogm.modules.PassiveModule;
import net.runeduniverse.libs.rogm.parser.Parser;
import net.runeduniverse.libs.rogm.pipeline.transaction.Assembler;

public class EntityFactory  {
	protected final Assembler assembler;
	protected final UniversalLogger logger;
	protected final IBuffer buffer;
	protected final Language.Instance lang;
	protected final Parser.Instance paser;
	protected final Module.Instance<?> module;
	protected final Collection<PassiveModule> passiveModules = new HashSet<>();

	public EntityFactory(final Assembler assembler, final Logger parentLogger, final IBuffer buffer,
			final Language.Instance lang, final Parser.Instance paser, final Module.Instance<?> module,
			final PassiveModule... passiveModules) {
		this.assembler = assembler;
		this.logger = new UniversalLogger(EntityFactory.class, parentLogger);
		this.buffer = buffer;
		this.lang = lang;
		this.paser = paser;
		this.module = module;
		this.passiveModules.addAll(Arrays.asList(passiveModules));
	}
}
