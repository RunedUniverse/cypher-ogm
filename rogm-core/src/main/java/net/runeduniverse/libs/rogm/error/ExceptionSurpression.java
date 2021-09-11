package net.runeduniverse.libs.rogm.error;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ExceptionSurpression extends Exception {
	private static final long serialVersionUID = 215607527815606000L;

	public ExceptionSurpression(String message) {
		super(message);
	}

	public ExceptionSurpression(String message, boolean trunk) {
		this(trunk ? "[TRUNKED] " + message : message);
		if (!trunk)
			return;

		List<StackTraceElement> trace = Arrays.asList(this.getStackTrace());
		this.trunkStackTrace(trace);
		this.setStackTrace(trace.toArray(new StackTraceElement[trace.size()]));
	}

	public ExceptionSurpression addSuppressed(Collection<Exception> errors) {
		for (Exception e : errors)
			this.addSuppressed(e);
		return this;
	}

	protected void trunkStackTrace(List<StackTraceElement> trace) {
		trace.removeIf(ExceptionSurpression::removeJUnitStackTrace);
	}

	protected static boolean removeJUnitStackTrace(StackTraceElement element) {
		return ExceptionSurpression.pathStartsWith(element, "org.junit.");
	}

	protected static boolean pathStartsWith(StackTraceElement element, String prefix) {
		return element.getClass()
				.getPackage()
				.getName()
				.startsWith(prefix);
	}
}
