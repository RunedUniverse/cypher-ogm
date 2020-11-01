package net.runeduniverse.libs.rogm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogLevelRule extends TestWatcher {

	private Level oldLevel;
	private final Class<?> clazz;
	private final Level level;

	@Override
	protected void starting(Description description) {
		oldLevel = Logger.getLogger(clazz.getName()).getLevel();
		Logger.getLogger(clazz.getName()).setLevel(level);
	}

	@Override
	protected void finished(Description description) {
		Logger.getLogger(clazz.getName()).setLevel(oldLevel);
	}
}