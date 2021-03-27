package net.runeduniverse.libs.rogm.scanner;

@FunctionalInterface
public interface ResultConsumer {
	void accept(TypePattern pattern);
}
