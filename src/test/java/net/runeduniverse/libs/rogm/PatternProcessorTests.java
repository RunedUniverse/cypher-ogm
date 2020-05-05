package net.runeduniverse.libs.rogm;

import org.junit.Test;
import net.runeduniverse.libs.rogm.patterns.PatternProcessor;
import net.runeduniverse.libs.rogm.patterns.PatternProcessor.PatternProcessorBuilder;

public class PatternProcessorTests {
	
	@Test
	public void packageParser() {
		PatternProcessorBuilder builder = new PatternProcessorBuilder();
		builder.addPackage("net.runeduniverse.libs.rogm.model");
		PatternProcessor processor = builder.build();
		
		
		System.out.println(processor.toString());
	}
	
}
